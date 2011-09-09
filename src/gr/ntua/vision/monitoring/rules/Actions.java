package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.model.Event;

import java.util.UUID;


/**
 * The possible rule actions.
 */
public enum Actions
{
	/***/
	Store(String.class, Integer.class, Integer.class, Long.class),
	/***/
	PushAsIs(String.class),
	/***/
	PushAggregated(String.class, Integer.class, Integer.class, Long.class),
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
	 */
	public void apply(ActionHandler handler, Event event, Object[] arguments, UUID id)
	{
		switch( this )
		{
		case PushAggregated:
			handler.ensurePool( id, (Integer) arguments[1], (Integer) arguments[2], (Long) arguments[3] );
			handler.aggregateNPush( event, id, (String) arguments[0] );
			return;

		case PushAsIs:
			handler.pushEvent( event, (String) arguments[0] );
			return;

		case Store:
			handler.ensurePool( id, (Integer) arguments[1], (Integer) arguments[2], (Long) arguments[3] );
			handler.aggregateNStore( event, id, (String) arguments[0] );
			return;
		}
	}
}
