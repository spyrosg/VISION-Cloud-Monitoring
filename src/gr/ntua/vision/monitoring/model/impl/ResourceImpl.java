package gr.ntua.vision.monitoring.model.impl;

import gr.ntua.vision.monitoring.model.Resource;

import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * The resource implementation.
 */
public class ResourceImpl implements Resource
{
	/** resource's type. */
	private final String	type;
	/** resource's value */
	private double			value;
	/** container name */
	private final String	containerName;
	/** object name */
	private final String	objectName;
	/** tenant name */
	private final String	tenantName;


	/**
	 * c/tor.
	 * 
	 * @param type
	 * @param value
	 * @param containerName
	 * @param objectName
	 * @param tenantName
	 */
	public ResourceImpl(String type, double value, String containerName, String objectName, String tenantName)
	{
		this.type = type;
		this.value = value;
		this.containerName = containerName;
		this.objectName = objectName;
		this.tenantName = tenantName;
	}


	/**
	 * copy c/tor.
	 * 
	 * @param r
	 */
	public ResourceImpl(Resource r)
	{
		this.type = r.type();
		this.value = r.value();
		this.containerName = r.containerName();
		this.objectName = r.objectName();
		this.tenantName = r.tenantName();
	}


	/**
	 * c/tor.
	 * 
	 * @param rsc
	 * @throws JSONException
	 */
	public ResourceImpl(JSONObject rsc) throws JSONException
	{
		type = rsc.getString( "type" );
		value = rsc.getDouble( "value" );
		containerName = rsc.getString( "container" );
		objectName = rsc.optString( "object" );
		tenantName = rsc.getString( "tenant" );
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.JSONExchanged#toJSON()
	 */
	@Override
	public JSONObject toJSON() throws JSONException
	{
		JSONObject rsc = new JSONObject();

		rsc.put( "type", type );
		rsc.put( "value", value );
		rsc.put( "tenant", tenantName );
		rsc.put( "object", objectName );
		rsc.put( "container", containerName );

		return rsc;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Resource#type()
	 */
	@Override
	public String type()
	{
		return type;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Resource#value()
	 */
	@Override
	public double value()
	{
		return value;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Resource#containerName()
	 */
	@Override
	public String containerName()
	{
		return containerName;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Resource#objectName()
	 */
	@Override
	public String objectName()
	{
		return objectName;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Resource#tenantName()
	 */
	@Override
	public String tenantName()
	{
		return tenantName;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Resource#setValue(double)
	 */
	@Override
	public void setValue(double value)
	{
		this.value = value;
	}


	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( containerName == null ) ? 0 : containerName.hashCode() );
		result = prime * result + ( ( objectName == null ) ? 0 : objectName.hashCode() );
		result = prime * result + ( ( tenantName == null ) ? 0 : tenantName.hashCode() );
		result = prime * result + ( ( type == null ) ? 0 : type.hashCode() );
		long temp;
		temp = Double.doubleToLongBits( value );
		result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
		return result;
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if( this == obj ) return true;
		if( obj == null ) return false;
		if( getClass() != obj.getClass() ) return false;
		ResourceImpl other = (ResourceImpl) obj;
		if( containerName == null )
		{
			if( other.containerName != null ) return false;
		}
		else if( !containerName.equals( other.containerName ) ) return false;
		if( objectName == null )
		{
			if( other.objectName != null ) return false;
		}
		else if( !objectName.equals( other.objectName ) ) return false;
		if( tenantName == null )
		{
			if( other.tenantName != null ) return false;
		}
		else if( !tenantName.equals( other.tenantName ) ) return false;
		if( type == null )
		{
			if( other.type != null ) return false;
		}
		else if( !type.equals( other.type ) ) return false;
		if( Double.doubleToLongBits( value ) != Double.doubleToLongBits( other.value ) ) return false;
		return true;
	}


	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Resource o)
	{
		int c = type.compareTo( o.type() );
		if( c != 0 ) return c;
		c = containerName.compareTo( o.containerName() );
		if( c != 0 ) return c;
		return tenantName.compareTo( o.tenantName() );
	}


	/**
	 * merge the two resource lists.
	 * 
	 * @param a
	 * @param b
	 * @return a list with the merge result.
	 */
	public static List<Resource> merge(List<Resource> a, List<Resource> b)
	{
		Function<Resource, Resource> deepCopy = new Function<Resource, Resource>() {
			@Override
			public Resource apply(Resource arg0)
			{
				return new ResourceImpl( arg0 );
			}
		};

		List<Resource> resources = Lists.newArrayList( Iterables.concat(	Iterables.transform( a, deepCopy ),
																			Iterables.transform( b, deepCopy ) ) );
		return resources;
	}


	/**
	 * join the types of the resources given.
	 * 
	 * @param resources
	 *            resources to join their types.
	 * @param separator
	 *            custom separator.
	 * @return the string requested.
	 */
	public static String typesIn(List<Resource> resources, String separator)
	{
		return Joiner.on( separator ).skipNulls().join( Iterables.transform( resources, new Function<Resource, String>() {
			@Override
			public String apply(Resource arg0)
			{
				return arg0.type();
			}
		} ) );
	}
}
