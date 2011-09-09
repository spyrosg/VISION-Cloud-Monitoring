package gr.ntua.vision.monitoring.model.impl;

import gr.ntua.vision.monitoring.model.Location;

import org.json.JSONException;
import org.json.JSONObject;


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
	/** the network address */
	private final String	address;


	/**
	 * c/tor.
	 * 
	 * @param host
	 * @param service
	 * @param user
	 * @param address
	 */
	public LocationImpl(String host, String service, String user, String address)
	{
		this.host = host;
		this.service = service;
		this.user = user;
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
		service = json.getString( "service" );
		user = json.getString( "user" );
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
		obj.put( "address", address );

		return obj;
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
	 * @see gr.ntua.vision.monitoring.model.Location#netAddress()
	 */
	@Override
	public String netAddress()
	{
		return address;
	}
}
