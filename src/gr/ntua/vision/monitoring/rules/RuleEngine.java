package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.rules.parser.ActionSpec;
import gr.ntua.vision.monitoring.rules.parser.RuleSpec;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import com.google.common.collect.Maps;


/**
 * This is the rule engine implementation. This is a singleton object.
 */
public class RuleEngine implements Runnable, ActionHandler
{
	/** the rules registered. */
	private final Map<UUID, EventMatcher>	rules		= Maps.newHashMap();
	/** internal rule cache. */
	private EventMatcher[]					matchers	= null;
	/** the event queue. */
	private final ArrayBlockingQueue<Event>	eventQueue	= new ArrayBlockingQueue<Event>( 10000 );


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
		synchronized( rules )
		{
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
				synchronized( rules )
				{
					cache = matchers;
				}
				for( EventMatcher em : cache )
					if( em.matches( event ) ) //
						for( ActionSpec action : em.rule.actions )
							action.action.apply( this, event, action.arguments, em.rule.id );
			}
		}
		catch( InterruptedException x )
		{
			// ignore.
		}
	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.ActionHandler#ensurePool(java.util.UUID, int, int, long)
	 */
	@Override
	public void ensurePool(UUID pool, int minCount, int maxCount, long timeWindow)
	{
		// TODO Auto-generated method stub

	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.ActionHandler#aggregateNStore(gr.ntua.vision.monitoring.model.Event, java.util.UUID,
	 *      java.lang.String)
	 */
	@Override
	public void aggregateNStore(Event prototype, UUID pool, String key)
	{
		// TODO Auto-generated method stub

	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.ActionHandler#pushEvent(gr.ntua.vision.monitoring.model.Event, java.lang.String)
	 */
	@Override
	public void pushEvent(Event prototype, String pushURL)
	{
		// TODO Auto-generated method stub

	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.ActionHandler#aggregateNPush(gr.ntua.vision.monitoring.model.Event, java.util.UUID,
	 *      java.lang.String)
	 */
	@Override
	public void aggregateNPush(Event prototype, UUID pool, String pushURL)
	{
		// TODO Auto-generated method stub

	}
}
