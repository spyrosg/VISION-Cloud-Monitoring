package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.impl.ResourceImpl;


/** the event fields that may checked against. */
public enum EventField implements CheckedField
{
	/***/
	Type(false),
	/***/
	Description(false),
	/***/
	Resource(false),
	/***/
	Source(true),
	/***/
	Target(true),
	/***/
	Observer(true),
	/***/
	;

	/** when <code>true</code> an inner field is required. */
	final boolean	hasInner;


	/**
	 * @param hasInner
	 */
	private EventField(boolean hasInner)
	{
		this.hasInner = hasInner;
	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.CheckedField#fieldValue(java.lang.Object)
	 */
	@Override
	public Object fieldValue(Object source)
	{
		switch( this )
		{
		case Observer:
			return ( (Event) source ).observer();
		case Resource:
			return ResourceImpl.typesIn( ( (Event) source ).resources(), ":" );
		case Source:
			return ( (Event) source ).source();
		case Target:
			return ( (Event) source ).target();
		case Type:
			return ( (Event) source ).eventType();
		case Description:
			return ( (Event) source ).getDescription();
		}
		throw new AssertionError( "unreachable" );
	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.CheckedField#hasInner()
	 */
	@Override
	public boolean hasInner()
	{
		return hasInner;
	}
}
