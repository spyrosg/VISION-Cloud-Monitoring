package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.model.Event;

import java.util.UUID;

import com.google.common.base.Function;


/**
 * The possible rule actions.
 */
public enum Actions
{
	/** store(url, minCount, timeWindow, field) */
	Store(String.class, Integer.class, Long.class, String.class),
	/** push(url, minCount, timeWindow, field) */
	PushAggregated(String.class, Integer.class, Long.class, String.class),
	/** push(url) */
	PushAsIs(String.class),
	/***/
	;

	/** the argument types. */
	private final Class< ? >[]	arg_types;


	/**
	 * c/tor.
	 * 
	 * @param arg_types
	 */
	private Actions(Class< ? >... arg_types)
	{
		this.arg_types = arg_types;
	}


	/**
	 * @return the arg_types
	 */
	public Class< ? >[] getArgumentTypes()
	{
		return arg_types;
	}


	/**
	 * apply the action over the given handler.
	 * 
	 * @param handler
	 *            the handler.
	 * @param event
	 *            the event to apply the action to.
	 * @param arguments
	 *            action arguments.
	 * @param id
	 *            id of rule, used to identify its aggregation pool - if any.
	 * @param actionFunctor
	 *            the action functor.
	 * @return <code>true</code> if and only if the appliation was successful.
	 */
	public boolean apply(ActionHandler handler, Event event, Object[] arguments, UUID id, Function<Event, Boolean> actionFunctor)
	{
		switch( this )
		{
		case PushAggregated:
		case Store:
			return handler.pool( id, actionFunctor, (Integer) arguments[1], (Long) arguments[2],
							EventField.valueOf( (String) arguments[3] ) ).push( event );

		case PushAsIs:
			return handler.transmit( event, (String) arguments[0] );
		}
		
		return false;
	}
}
