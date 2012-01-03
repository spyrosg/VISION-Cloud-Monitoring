package gr.ntua.vision.monitoring.model.impl;

import gr.ntua.vision.monitoring.model.Location;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


/**
 * Implementation of the location interface.
 */
public class LocationImpl implements Location
{
	/** the host name */
	private final String	host;
	/** the service name */
	private final String	service;
	/** the user ID */
	private final String	user;
	/** the tenant ID */
	private final String	tenant;
	/** the network address */
	private final String	address;


	/**
	 * c/tor.
	 * 
	 * @param host
	 * @param service
	 * @param user
	 * @param tenant
	 * @param address
	 */
	public LocationImpl(String host, String service, String user, String tenant, String address)
	{
		this.host = host;
		this.service = service;
		this.user = user;
		this.tenant = tenant;
		this.address = address;
	}


	/**
	 * c/tor.
	 * 
	 * @param json
	 * @throws JSONException
	 */
	public LocationImpl(JSONObject json) throws JSONException
	{
		host = json.getString( "host" );
		service = json.optString( "service" );
		user = json.optString( "user" );
		tenant = json.optString( "tenant" );
		address = json.getString( "address" );
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.JSONExchanged#toJSON()
	 */
	@Override
	public JSONObject toJSON() throws JSONException
	{
		JSONObject obj = new JSONObject();

		obj.put( "host", host );
		obj.put( "service", service );
		obj.put( "user", user );
		obj.put( "tenant", tenant );
		obj.put( "address", address );

		return obj;
	}


	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( address == null ) ? 0 : address.hashCode() );
		result = prime * result + ( ( host == null ) ? 0 : host.hashCode() );
		result = prime * result + ( ( service == null ) ? 0 : service.hashCode() );
		result = prime * result + ( ( tenant == null ) ? 0 : tenant.hashCode() );
		result = prime * result + ( ( user == null ) ? 0 : user.hashCode() );
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
		LocationImpl other = (LocationImpl) obj;
		if( address == null )
		{
			if( other.address != null ) return false;
		}
		else if( !address.equals( other.address ) ) return false;
		if( host == null )
		{
			if( other.host != null ) return false;
		}
		else if( !host.equals( other.host ) ) return false;
		if( service == null )
		{
			if( other.service != null ) return false;
		}
		else if( !service.equals( other.service ) ) return false;
		if( tenant == null )
		{
			if( other.tenant != null ) return false;
		}
		else if( !tenant.equals( other.tenant ) ) return false;
		if( user == null )
		{
			if( other.user != null ) return false;
		}
		else if( !user.equals( other.user ) ) return false;
		return true;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Location#hostname()
	 */
	@Override
	public String hostname()
	{
		return host;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Location#service()
	 */
	@Override
	public String service()
	{
		return service;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Location#userID()
	 */
	@Override
	public String userID()
	{
		return user;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Location#tenantID()
	 */
	@Override
	public String tenantID()
	{
		return tenant;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Location#netAddress()
	 */
	@Override
	public String netAddress()
	{
		return address;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "LocationImpl [host=" );
		builder.append( host );
		builder.append( ", service=" );
		builder.append( service );
		builder.append( ", user=" );
		builder.append( user );
		builder.append( ", tenant=" );
		builder.append( tenant );
		builder.append( ", address=" );
		builder.append( address );
		builder.append( "]" );
		return builder.toString();
	}
}
