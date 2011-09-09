package gr.ntua.vision.monitoring.model.impl;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.Location;
import gr.ntua.vision.monitoring.model.Resource;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Base implementation of the event interface.
 */
public class EventImpl implements Event
{
	/** event id. */
	private final UUID			id;
	/** probe id. */
	private final UUID			probe;
	/** tenant id */
	private final String		tenant;
	/** user id */
	private final String		user;
	/** event description. */
	private final String		description;
	/** event's value. */
	private final Object		value;
	/** event's type. */
	private final EventType		type;
	/** event's resource type. */
	private final ResourceType	resource;
	/** begin time of the event. */
	private final long			start;
	/** end time of the event. */
	private final long			end;
	/** event's source. */
	private final Location		source;
	/** event's target. */
	private final Location		target;
	/** event's observer. */
	private final Location		observer;


	/**
	 * c/tor.
	 * 
	 * @param id
	 * @param probe
	 * @param tenant
	 * @param user
	 * @param description
	 * @param value
	 * @param type
	 * @param resource
	 * @param start
	 * @param end
	 * @param source
	 * @param target
	 * @param observer
	 */
	public EventImpl(UUID id, UUID probe, String tenant, String user, String description, Object value, EventType type,
			ResourceType resource, long start, long end, Location source, Location target, Location observer)
	{
		this.id = id;
		this.probe = probe;
		this.tenant = tenant;
		this.user = user;
		this.description = description;
		this.value = value;
		this.type = type;
		this.resource = resource;
		this.start = start;
		this.end = end;
		this.source = source;
		this.target = target;
		this.observer = observer;
	}


	/**
	 * c/tor.
	 * 
	 * @param json
	 * @throws JSONException
	 */
	public EventImpl(JSONObject json) throws JSONException
	{
		id = UUID.fromString( json.getString( "id" ) );
		probe = UUID.fromString( json.getString( "probe" ) );
		tenant = json.getString( "tenant" );
		user = json.getString( "user" );
		description = json.getString( "description" );
		type = EventType.valueOf( json.getString( "type" ) );
		start = Long.parseLong( json.getString( "start" ) );
		end = Long.parseLong( json.getString( "end" ) );

		String rsc_str = json.getString( "resource" );
		resource = rsc_str == null ? null : ResourceType.valueOf( rsc_str );

		if( resource != null )
			value = resource.parseValue( json.getString( "value" ) );
		else value = ActionStatus.valueOf( json.getString( "value" ) );

		JSONObject tmp = json.getJSONObject( "source" );
		source = new LocationImpl( tmp );
		tmp = json.getJSONObject( "target" );
		target = tmp == null ? null : new LocationImpl( tmp );
		tmp = json.getJSONObject( "observer" );
		observer = tmp == null ? null : new LocationImpl( tmp );
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.JSONExchanged#toJSON()
	 */
	@Override
	public JSONObject toJSON() throws JSONException
	{
		JSONObject obj = new JSONObject();

		obj.put( "id", id.toString() );
		obj.put( "probe", probe.toString() );
		obj.put( "tenant", tenant );
		obj.put( "user", user );
		obj.put( "description", description );
		obj.put( "type", type.toString() );
		obj.put( "start", start );
		obj.put( "end", end );
		obj.put( "resource", resource.toString() );
		obj.put( "value", value );
		obj.put( "source", source.toJSON() );
		obj.put( "target", target == null ? null : target.toJSON() );
		obj.put( "observer", observer == null ? null : observer.toJSON() );

		return obj;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#id()
	 */
	@Override
	public UUID id()
	{
		return id;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#probeID()
	 */
	@Override
	public UUID probeID()
	{
		return probe;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#tenantID()
	 */
	@Override
	public String tenantID()
	{
		return tenant;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#userID()
	 */
	@Override
	public String userID()
	{
		return user;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return description;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#getValue()
	 */
	@Override
	public Object getValue()
	{
		return value;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#eventType()
	 */
	@Override
	public EventType eventType()
	{
		return type;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#resourceType()
	 */
	@Override
	public Resource resourceType()
	{
		return resource;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#startTime()
	 */
	@Override
	public long startTime()
	{
		return start;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#endTime()
	 */
	@Override
	public long endTime()
	{
		return end;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#source()
	 */
	@Override
	public Location source()
	{
		return source;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#target()
	 */
	@Override
	public Location target()
	{
		return target;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#observer()
	 */
	@Override
	public Location observer()
	{
		return observer;
	}
}
