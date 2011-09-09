package gr.ntua.vision.monitoring.rules.parser;

import java.util.regex.Pattern;

import gr.ntua.vision.monitoring.model.Event;


/**
 * This specifies the check to perform over a field of an {@link Event} class.
 */
public class FieldCheck
{
	/** the field name. */
	public final CheckedField	field;
	/** flag that indicates whether the field value should be the one specified here, or match it as a regular expression. */
	public final boolean		exact;
	/** the field value to check against (can be a regular expression), Exactly one of this and {@link #value} is <code>null</code> */
	public final String			value;
	/** inner field to check. Exactly one of this and {@link #value} is <code>null</code>. */
	public final FieldCheck		inner;
	/** when the value is regular expression, this contains it (compiled). */
	public final Pattern		pattern;


	/**
	 * c/tor.
	 * 
	 * @param field
	 * @param exact
	 * @param value
	 * @param isInner
	 */
	FieldCheck(String field, boolean exact, String value, boolean isInner)
	{
		if( isInner )
			this.field = LocationField.valueOf( field );
		else this.field = EventField.valueOf( field );

		if( this.field.hasInner() )
			throw new AssertionError( "Field '" + field + "' specified has inner fields, but they are not looked for." );

		this.exact = exact;
		this.value = value;
		this.inner = null;

		if( exact )
			this.pattern = null;
		else this.pattern = Pattern.compile( value );
	}


	/**
	 * c/tor.
	 * 
	 * @param field
	 * @param inner
	 */
	FieldCheck(String field, FieldCheck inner)
	{
		this.field = EventField.valueOf( field );

		if( !this.field.hasInner() ) throw new AssertionError( "Field '" + field + "' specified does not have inner fields." );

		this.exact = false;
		this.value = null;
		this.inner = inner;
		this.pattern = null;
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
