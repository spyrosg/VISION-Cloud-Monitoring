package gr.ntua.vision.monitoring.rules.parser;

import java.util.Arrays;


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
}
