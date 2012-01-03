package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.ext.catalog.Catalog;
import gr.ntua.vision.monitoring.ext.catalog.GlobalCatalogFactory;
import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.rules.parser.ActionSpec;
import gr.ntua.vision.monitoring.rules.parser.RuleSpec;

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
public class RuleEngine implements ActionHandler, Runnable
{
	/** identifier of pools. */
	class PoolKey implements Comparable<PoolKey>
	{
		/** the rule id */
		final UUID	id;
		/** the referring action index. */
		final int	order;


		/**
		 * c/tor.
		 * 
		 * @param id
		 * @param order
		 */
		PoolKey(UUID id, int order)
		{
			super();
			this.id = id;
			this.order = order;
		}


		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
			result = prime * result + order;
			return result;
		}


		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if( this == obj ) return true;
			if( obj == null ) return false;
			if( getClass() != obj.getClass() ) return false;
			PoolKey other = (PoolKey) obj;
			if( id == null )
			{
				if( other.id != null ) return false;
			}
			else if( !id.equals( other.id ) ) return false;
			if( order != other.order ) return false;
			return true;
		}


		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(PoolKey arg0)
		{
			int result = id.compareTo( arg0.id );
			if( result != 0 ) return result;
			return order - arg0.order;
		}


		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return "[" + id + " / " + order + "]";
		}
	}

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
	private final Map<PoolKey, AggregationPool>	pools		= Maps.newHashMap();
	/** internal rule cache. */
	private EventMatcher[]						matchers	= null;
	/** the event queue. */
	private final ArrayBlockingQueue<Event>		eventQueue	= new ArrayBlockingQueue<Event>( 10000 );
	/** the host thread */
	private Thread								hostTh		= null;

	static
	{
		client.setConnectTimeout( 1000 );
	}


	/**
	 * c/tor.
	 */
	public RuleEngine()
	{
		// NOP
	}


	/**
	 * start the thread.
	 */
	public void start()
	{
		hostTh = new Thread( this );
		hostTh.setName( "RuleEngine" );
		hostTh.setDaemon( true );
		hostTh.start();
	}


	/**
	 * check if the host thread is alive.
	 * 
	 * @return <code>true</code> if and only if the host thread is alive.
	 */
	public boolean isAlive()
	{
		return hostTh != null && hostTh.isAlive();
	}


	/**
	 * shutdown the engine.
	 * 
	 * @throws InterruptedException
	 */
	public void shutdown() throws InterruptedException
	{
		log.info( "shutdown" );
		if( hostTh != null )
		{
			hostTh.interrupt();
			hostTh.join();
			hostTh = null;
		}

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
				try
				{
					Event event = eventQueue.take();
					if( event == null ) continue;
					// log.debug( ">> Event goes through rule chain: " + event );

					EventMatcher[] cache = null;
					synchronized( rulesLock )
					{
						cache = matchers;
					}
					int matches = 0;
					int actions = 0;
					if( cache != null ) //
						for( EventMatcher em : cache )
							if( em.matches( event ) )
							{
								++matches;
								log.debug( "SUCCESS: Event match occurred, performing " + em.rule.actions.length + " actions." );
								for( int i = 0; i < em.rule.actions.length; ++i )
									try
									{
										ActionSpec action = em.rule.actions[i];
										if( !action.action.apply(	this, event, action.arguments, em.rule.id, i,
																	action.actionFunctor( this ) ) ) //
											remove( em.rule.id );
										++actions;
									}
									catch( Throwable x )
									{
										x.printStackTrace();
										remove( em.rule.id );
									}
							}

					if( cache != null ) //
						log.debug( "Event matched to " + matches + "/" + cache.length + "  actions exec()ed: " + actions );
				}
				catch( InterruptedException x )
				{
					break;
				}
				catch( Throwable x )
				{
					x.printStackTrace();
				}
		}
		finally
		{
			log.debug( "RULE ENGINE STOPS" );
		}
	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.ActionHandler#pool(java.util.UUID, int, com.google.common.base.Function, int, long,
	 *      gr.ntua.vision.monitoring.rules.CheckedField[])
	 */
	@Override
	public AggregationPool pool(UUID pool, int order, Function<Event, Boolean> action, int maxCount, long timeWindow,
			CheckedField... fields)
	{
		PoolKey key = new PoolKey( pool, order );
		AggregationPool ret = null;
		synchronized( rulesLock )
		{
			ret = pools.get( key );
			if( ret == null )
			{
				pools.put( key, new AggregationPool( pool, maxCount, timeWindow, fields, action ) );
				ret = pools.get( key );
			}
		}
		log.debug( key + " :: " + ( ret == null ? "null" : Integer.toString( ret.hashCode() ) ) );
		return ret;
	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.ActionHandler#store(gr.ntua.vision.monitoring.model.Event, java.lang.String)
	 */
	@Override
	public boolean store(Event event, String key)
	{
		log.debug( "store " + event.id() + " @ " + key );

		Catalog catalog = GlobalCatalogFactory.globalCatalogInstance();

		catalog.put( key, event.startTime(), event.serialize() );

		return true;
	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.ActionHandler#transmit(gr.ntua.vision.monitoring.model.Event, java.lang.String)
	 */
	@Override
	public boolean transmit(Event event, String pushURL)
	{
		log.debug( "trasmit " + event.id() + " @ " + pushURL );
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
