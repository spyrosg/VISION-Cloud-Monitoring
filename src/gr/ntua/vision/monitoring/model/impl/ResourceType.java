package gr.ntua.vision.monitoring.model.impl;

import gr.ntua.vision.monitoring.model.Resource;


/**
 * This models the monitored resources.
 */
public enum ResourceType implements Resource
{
	/***/
	CPU(Float.class),
	/***/
	SystemLoad(Float.class),
	/***/
	MemoryUsage(Float.class/*percentage*/),
	/***/
	FreeMemory(Long.class),
	/***/
	BandwidthUsage(Float.class/*percentage*/),
	/***/
	UsedBandwidth(Long.class/*bytes / sec*/),
	/***/
	ServiceStatus(Boolean.class/* alive / dead */),
	/***/
	ServiceResponce(Integer.class/* response time in seconds. */)
	/***/
	;

	/** the type of the measurement objects. This method always returns the box type of a java builtin type. */
	private final Class< ? >	measurement_type;


	/**
	 * c/tor.
	 * 
	 * @param measurement_type
	 *            the type of the measurement objects. This method always returns the box type of a java builtin type.
	 */
	private ResourceType(Class< ? > measurement_type)
	{
		this.measurement_type = measurement_type;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Resource#resourceName()
	 */
	@Override
	public String resourceName()
	{
		return toString();
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Resource#measurementType()
	 */
	@Override
	public Class< ? > measurementType()
	{
		return measurement_type;
	}


	/**
	 * parse a value from its string form.
	 * 
	 * @param value
	 *            the string form of the value.
	 * @return the value.
	 */
	public Object parseValue(String value)
	{
		if( value == null ) return null;
		if( measurement_type == Boolean.class ) return Boolean.parseBoolean( value );
		if( measurement_type == Byte.class ) return Byte.parseByte( value );
		if( measurement_type == Character.class ) return value.charAt( 0 );
		if( measurement_type == Short.class ) return Short.parseShort( value );
		if( measurement_type == Integer.class ) return Integer.parseInt( value );
		if( measurement_type == Long.class ) return Long.parseLong( value );
		if( measurement_type == Float.class ) return Float.parseFloat( value );
		if( measurement_type == Double.class ) return Double.parseDouble( value );
		throw new AssertionError( "Bad measurement type: not builtin." );
	}
}
