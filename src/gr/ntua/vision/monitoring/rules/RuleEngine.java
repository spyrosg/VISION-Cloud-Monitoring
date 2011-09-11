package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.rules.parser.ActionSpec;
import gr.ntua.vision.monitoring.rules.parser.RuleSpec;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import com.google.common.base.Function;
import com.google.common.collect.Maps;


/**
 * This is the rule engine implementation. This is a singleton object.
 */
public class RuleEngine implements Runnable, ActionHandler
{
	/** rules & pools lock. */
	private final Object						rulesLock = new Object();
	/** the rules registered. */
	private final Map<UUID, EventMatcher>		rules		= Maps.newHashMap();
	/** the pools registered. */
	private final Map<UUID, AggregationPool>	pools		= Maps.newHashMap();
	/** internal rule cache. */
	private EventMatcher[]						matchers	= null;
	/** the event queue. */
	private final ArrayBlockingQueue<Event>		eventQueue	= new ArrayBlockingQueue<Event>( 10000 );


	/**
	 * register a rule.
	 * 
	 * @param rule
	 *            the rule to register.
	 */
	public void register(RuleSpec rule)
	{
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
				for( EventMatcher em : cache )
					if( em.matches( event ) ) //
						for( ActionSpec action : em.rule.actions )
							action.action.apply( this, event, action.arguments, em.rule.id, action.actionFunctor( this ) );
			}
		}
		catch( InterruptedException x )
		{
			// ignore.
		}
	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.ActionHandler#pool(java.util.UUID, com.google.common.base.Function, int, long,
	 *      gr.ntua.vision.monitoring.rules.CheckedField[])
	 */
	@Override
	public AggregationPool pool(UUID pool, Function<Event, Void> action, int maxCount, long timeWindow, CheckedField... fields)
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
	public void store(Event event, String key)
	{
		// TODO Auto-generated method stub

	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.ActionHandler#transmit(gr.ntua.vision.monitoring.model.Event, java.lang.String)
	 */
	@Override
	public void transmit(Event event, String pushURL)
	{
		// TODO Auto-generated method stub

	}
}
