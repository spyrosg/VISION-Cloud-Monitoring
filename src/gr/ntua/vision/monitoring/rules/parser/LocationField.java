package gr.ntua.vision.monitoring.rules.parser;

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
	Service,
	/***/
	;

	/**
	 * @see gr.ntua.vision.monitoring.rules.parser.CheckedField#fieldValue(java.lang.Object)
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
		}
		throw new AssertionError( "unreachable" );
	}


	/**
	 * @see gr.ntua.vision.monitoring.rules.parser.CheckedField#hasInner()
	 */
	@Override
	public boolean hasInner()
	{
		return false;
	}
}
