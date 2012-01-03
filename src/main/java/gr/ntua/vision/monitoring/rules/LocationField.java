package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.model.Location;


/** the location fields that may checked against. */
public enum LocationField implements CheckedField
{
	/***/
	Host,
	/***/
	Address,
	/***/
	User,
	/***/
	Tenant,
	/***/
	Service,
	/***/
	;

	/**
	 * @see gr.ntua.vision.monitoring.rules.CheckedField#fieldValue(java.lang.Object)
	 */
	@Override
	public Object fieldValue(Object source)
	{
		switch( this )
		{
		case Address:
			return ( (Location) source ).netAddress();
		case Host:
			return ( (Location) source ).hostname();
		case Service:
			return ( (Location) source ).service();
		case User:
			return ( (Location) source ).userID();
		case Tenant:
			return ( (Location) source ).tenantID();
		}
		throw new AssertionError( "unreachable" );
	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.CheckedField#hasInner()
	 */
	@Override
	public boolean hasInner()
	{
		return false;
	}
}
