package gr.ntua.vision.monitoring.rules.parser;

import gr.ntua.vision.monitoring.model.Event;


/** the event fields that may checked against. */
public enum EventField implements CheckedField
{
	/***/
	Tenant(false),
	/***/
	User(false),
	/***/
	Type(false),
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
	 * @see gr.ntua.vision.monitoring.rules.parser.CheckedField#fieldValue(java.lang.Object)
	 */
	@Override
	public Object fieldValue(Object source)
	{
		switch( this )
		{
		case Observer:
			return ( (Event) source ).observer();
		case Resource:
			return ( (Event) source ).resourceType();
		case Source:
			return ( (Event) source ).source();
		case Target:
			return ( (Event) source ).target();
		case Tenant:
			return ( (Event) source ).tenantID();
		case Type:
			return ( (Event) source ).eventType();
		case User:
			return ( (Event) source ).userID();
		}
		throw new AssertionError( "unreachable" );
	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.parser.CheckedField#hasInner()
	 */
	@Override
	public boolean hasInner()
	{
		return hasInner;
	}
}
