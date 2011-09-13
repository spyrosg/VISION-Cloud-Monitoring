package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.ext.local.Catalog;
import gr.ntua.vision.monitoring.ext.local.CloudCatalogFactory;
import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.rules.parser.ActionSpec;
import gr.ntua.vision.monitoring.rules.parser.RuleSpec;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.representation.Form;


/**
 * This is the rule engine implementation. This is a singleton object.
 */
public class RuleEngine extends Thread implements ActionHandler
{
	/** the logger. */
	@SuppressWarnings("all")
	private static final Logger					log			= Logger.getLogger( RuleEngine.class );
	/** the client used to make HTTP requests. */
	private static final Client					client		= Client.create();
	/** rules & pools lock. */
	private final Object						rulesLock	= new Object();
	/** the rules registered. */
	private final Map<UUID, EventMatcher>		rules		= Maps.newHashMap();
	/** the pools registered. */
	private final Map<UUID, AggregationPool>	pools		= Maps.newHashMap();
	/** internal rule cache. */
	private EventMatcher[]						matchers	= null;
	/** the event queue. */
	private final ArrayBlockingQueue<Event>		eventQueue	= new ArrayBlockingQueue<Event>( 10000 );

	static
	{
		client.setConnectTimeout( 1000 );
	}


	/**
	 * c/tor.
	 */
	public RuleEngine()
	{
		setName( "RuleEngine" );
	}


	/**
	 * shutdown the engine.
	 * 
	 * @throws InterruptedException
	 */
	public void shutdown() throws InterruptedException
	{
		log.info( "shutdown" );
		interrupt();
		join();

		synchronized( rulesLock )
		{
			for( AggregationPool pool : pools.values() )
				try
				{
					pool.shutdown();
				}
				catch( InterruptedException x )
				{
					x.printStackTrace();
				}

			rules.clear();
			pools.clear();
		}
	}


	/**
	 * register a rule.
	 * 
	 * @param rule
	 *            the rule to register.
	 */
	public void register(RuleSpec rule)
	{
		log.debug( "registering rule: " + rule.id );
		rules.put( rule.id, new EventMatcher( rule, rule.normalizeChecks() ) );
		synchronized( rules )
		{
			matchers = rules.values().toArray( new EventMatcher[rules.size()] );
		}
	}


	/**
	 * get a rule by ID.
	 * 
	 * @param id
	 *            the rule's ID.
	 * @return the rule, or <code>null</code> if the ID .
	 */
	public RuleSpec rule(UUID id)
	{
		EventMatcher m = rules.get( id );

		return m == null ? null : m.rule;
	}


	/**
	 * remove a registered rule
	 * 
	 * @param id
	 *            the rule's ID.
	 * @return if a rule existed with the given id.
	 */
	public boolean remove(UUID id)
	{
		log.debug( "removing rule w/ id: " + id );
		boolean ret = null != rules.remove( id );
		synchronized( rulesLock )
		{
			pools.remove( id );
			matchers = rules.values().toArray( new EventMatcher[rules.size()] );
		}
		return ret;
	}


	/**
	 * push an event in the queue.
	 * 
	 * @param e
	 *            the event to push.
	 */
	public void push(Event e)
	{
		if( e == null ) return;
		eventQueue.offer( e );
	}


	/**
	 * push the given events in the queue.
	 * 
	 * @param events
	 *            the events to push.
	 */
	public void push(Iterable<Event> events)
	{
		for( Event e : events )
			eventQueue.offer( e );
	}


	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		log.debug( "rule engine starts" );
		try
		{
			while( true )
			{
				Event event = eventQueue.take();

				EventMatcher[] cache = null;
				synchronized( rulesLock )
				{
					cache = matchers;
				}
				if( cache != null )
				//
					for( EventMatcher em : cache )
						if( em.matches( event ) )
						//
							for( ActionSpec action : em.rule.actions )
								try
								{
									if( !action.action.apply(	this, event, action.arguments, em.rule.id,
																action.actionFunctor( this ) ) ) remove( em.rule.id );
								}
								catch( Throwable x )
								{
									x.printStackTrace();
									remove( em.rule.id );
								}
			}
		}
		catch( InterruptedException x )
		{
			// ignore.
		}
		log.debug( "rule engine stops" );
	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.ActionHandler#pool(java.util.UUID, com.google.common.base.Function, int, long,
	 *      gr.ntua.vision.monitoring.rules.CheckedField[])
	 */
	@Override
	public AggregationPool pool(UUID pool, Function<Event, Boolean> action, int maxCount, long timeWindow, CheckedField... fields)
	{
		synchronized( rulesLock )
		{
			if( !pools.containsKey( pool ) ) //
				pools.put( pool, new AggregationPool( pool, maxCount, timeWindow, fields, action ) );
			return pools.get( pool );
		}
	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.ActionHandler#store(gr.ntua.vision.monitoring.model.Event, java.lang.String)
	 */
	@Override
	public boolean store(Event event, String key)
	{
		log.trace( "store " + event.id() + " @ " + key );

		Catalog catalog = CloudCatalogFactory.cloudCatalogInstance();

		catalog.put( key, new Date().getTime(), event.serialize() );

		return true;
	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.ActionHandler#transmit(gr.ntua.vision.monitoring.model.Event, java.lang.String)
	 */
	@Override
	public boolean transmit(Event event, String pushURL)
	{
		log.trace( "trasmit " + event.id() + " @ " + pushURL );
		try
		{
			Form form = new Form();
			form.add( "event", event.toJSON().toString() );

			client.resource( pushURL ).post( form );
			return true;
		}
		catch( Throwable x )
		{
			x.printStackTrace();
			return false;
		}
	}
}
