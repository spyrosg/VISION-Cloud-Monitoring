package gr.ntua.vision.monitoring.rules.parser;

import gr.ntua.vision.monitoring.model.Event;


/**
 * This specifies the check to perform over a field of an {@link Event} class.
 */
public class FieldCheck
{
	/** the field name. */
	public final String		field;
	/** flag that indicates whether the field value should be the one specified here, or match it as a regular expression. */
	public final boolean	exact;
	/** the field value to check against (can be a regular expression), Exactly one of this and {@link #value} is <code>null</code> */
	public final String		value;
	/** inner field to check. Exactly one of this and {@link #value} is <code>null</code>. */
	public final FieldCheck	inner;


	/**
	 * c/tor.
	 * 
	 * @param field
	 * @param exact
	 * @param value
	 */
	FieldCheck(String field, boolean exact, String value)
	{
		this.field = field;
		this.exact = exact;
		this.value = value;
		this.inner = null;
	}


	/**
	 * c/tor.
	 * 
	 * @param field
	 * @param inner
	 */
	FieldCheck(String field, FieldCheck inner)
	{
		this.field = field;
		this.exact = false;
		this.value = null;
		this.inner = inner;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "FieldCheck [field=" );
		builder.append( field );
		builder.append( ", exact=" );
		builder.append( exact );
		builder.append( ", value=" );
		builder.append( value );
		builder.append( ", inner=" );
		builder.append( inner );
		builder.append( "]" );
		return builder.toString();
	}
}
