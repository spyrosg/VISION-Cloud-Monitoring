package gr.ntua.vision.monitoring.rules.parser;

import java.util.Arrays;
import java.util.UUID;


/**
 * The rule specification.
 */
public class RuleSpec
{
	/** rule name. */
	public final String			name;
	/** rule ID. */
	public final UUID			id;
	/** event field checks. */
	public final FieldCheck[][]	checks;
	/** actions to perform. */
	public final ActionSpec[]	actions;


	/**
	 * c/tor.
	 * 
	 * @param name
	 * @param checks
	 * @param actions
	 */
	RuleSpec(String name, FieldCheck[][] checks, ActionSpec[] actions)
	{
		this.name = name;
		this.id = UUID.randomUUID();
		this.checks = checks;
		this.actions = actions;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "RuleSpec [name=" );
		builder.append( name );
		builder.append( ", id=" );
		builder.append( id );
		builder.append( ", checks=" );
		builder.append( Arrays.toString( checks ) );
		builder.append( ", actions=" );
		builder.append( Arrays.toString( actions ) );
		builder.append( "]" );
		return builder.toString();
	}
}
