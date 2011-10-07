package gr.ntua.vision.monitoring.model.impl;

import gr.ntua.vision.monitoring.model.Resource;

import java.util.Collections;
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
	/** resource's unit */
	private final String	unit;
	/** resource's value */
	private double			value;


	/**
	 * c/tor.
	 * 
	 * @param type
	 * @param unit
	 * @param value
	 */
	public ResourceImpl(String type, String unit, double value)
	{
		this.type = type;
		this.unit = unit;
		this.value = value;
	}


	/**
	 * copy c/tor.
	 * 
	 * @param r
	 */
	public ResourceImpl(Resource r)
	{
		this.type = r.type();
		this.unit = r.unit();
		this.value = r.value();
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
		unit = rsc.getString( "unit" );
		value = rsc.getDouble( "value" );
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.JSONExchanged#toJSON()
	 */
	@Override
	public JSONObject toJSON() throws JSONException
	{
		JSONObject rsc = new JSONObject();

		rsc.put( "type", type );
		rsc.put( "unit", unit );
		rsc.put( "value", value );

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
	 * @see gr.ntua.vision.monitoring.model.Resource#unit()
	 */
	@Override
	public String unit()
	{
		return unit;
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
		result = prime * result + ( ( type == null ) ? 0 : type.hashCode() );
		result = prime * result + ( ( unit == null ) ? 0 : unit.hashCode() );
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
		if( type == null )
		{
			if( other.type != null ) return false;
		}
		else if( !type.equals( other.type ) ) return false;
		if( unit == null )
		{
			if( other.unit != null ) return false;
		}
		else if( !unit.equals( other.unit ) ) return false;
		if( Double.doubleToLongBits( value ) != Double.doubleToLongBits( other.value ) ) return false;
		return true;
	}


	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Resource o)
	{
		return type.compareTo( o.type() );
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
		merge( resources );
		return resources;
	}


	/**
	 * self-merge the given list of resources by creating a single resource per type.
	 * 
	 * @param resources
	 *            the resource list to merge.
	 */
	public static void merge(List<Resource> resources)
	{
		Collections.sort( resources );

		Resource prev = resources.get( 0 );
		for( int i = 1; i < resources.size(); ++i )
		{
			Resource cur = resources.get( i );

			if( cur.type().equals( prev.type() ) )
			{
				prev.setValue( prev.value() + cur.value() );
				resources.remove( i );
				--i;
			}
			else prev = cur;
		}
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
