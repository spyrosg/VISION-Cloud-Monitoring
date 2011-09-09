package gr.ntua.vision.monitoring.rules.parser;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.rules.ActionHandler;
import gr.ntua.vision.monitoring.rules.Actions;

import java.util.Arrays;

import com.google.common.base.Function;


/**
 * The action specification.
 */
public class ActionSpec
{
	/** the action. */
	public final Actions	action;
	/** the action arguments. */
	public final Object[]	arguments;


	/**
	 * @param action
	 * @param arguments
	 */
	ActionSpec(Actions action, Object[] arguments)
	{
		this.action = action;
		this.arguments = arguments;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "ActionSpec [action=" );
		builder.append( action );
		builder.append( ", arguments=" );
		builder.append( Arrays.toString( arguments ) );
		builder.append( "]" );
		return builder.toString();
	}


	/**
	 * create the action handler.
	 * 
	 * @param handler
	 *            the action handler.
	 * @return the functor.
	 */
	public Function<Event, Void> actionFunctor(final ActionHandler handler)
	{
		return new Function<Event, Void>() {
			@Override
			public Void apply(Event event)
			{
				switch( action )
				{
				case PushAggregated:
					handler.transmit( event, (String) arguments[0] );
					break;

				case Store:
					handler.store( event, (String) arguments[0] );
					break;

				default:
				}
				return null;
			}
		};
	}
}
