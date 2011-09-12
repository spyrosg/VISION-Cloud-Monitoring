package gr.ntua.vision.monitoring.model.impl;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.Location;
import gr.ntua.vision.monitoring.model.Resource;
import gr.ntua.vision.monitoring.util.Pair;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;


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
	private String				description;
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
	/** the aggregation count. */
	private int					aggregation_count;


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
	 * copy c/tor.
	 * 
	 * @param event
	 */
	public EventImpl(Event event)
	{
		this.id = event.id();
		this.probe = event.probeID();
		this.tenant = event.tenantID();
		this.user = event.userID();
		this.description = event.getDescription();
		this.value = event.getValue();
		this.type = event.eventType();
		this.resource = (ResourceType) event.resourceType();
		this.start = event.startTime();
		this.end = event.endTime();
		this.source = event.source();
		this.target = event.target();
		this.observer = event.observer();
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
		start = json.getLong( "start" );
		end = json.getLong( "end" );
		aggregation_count = json.getInt( "aggr_count" );

		String rsc_str = json.optString( "resource" );
		resource = rsc_str == null ? null : ResourceType.valueOf( rsc_str );

		if( resource != null )
			value = resource.parseValue( json.optString( "value" ) );
		else value = ActionStatus.valueOf( json.optString( "value" ) );

		JSONObject tmp = json.getJSONObject( "source" );
		source = new LocationImpl( tmp );
		tmp = json.optJSONObject( "target" );
		target = tmp == null ? null : new LocationImpl( tmp );
		tmp = json.optJSONObject( "observer" );
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
		obj.put( "resource", resource == null ? null : resource.toString() );
		obj.put( "value", value );
		obj.put( "aggr_count", aggregation_count );
		obj.put( "source", source.toJSON() );
		obj.put( "target", target == null ? null : target.toJSON() );
		obj.put( "observer", observer == null ? null : observer.toJSON() );

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
		result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
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
		EventImpl other = (EventImpl) obj;
		if( id == null )
		{
			if( other.id != null ) return false;
		}
		else if( !id.equals( other.id ) ) return false;
		return true;
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


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#aggregationCount()
	 */
	@Override
	public int aggregationCount()
	{
		return aggregation_count;
	}


	/**
	 * @param aggregation_count
	 *            the aggregation count to set
	 */
	public void setAggregationCount(int aggregation_count)
	{
		this.aggregation_count = aggregation_count;
	}


	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}


	/**
	 * @see gr.ntua.vision.monitoring.model.Event#serialize()
	 */
	@Override
	public List<Pair<String, Object>> serialize()
	{
		List<Pair<String, Object>> pairs = Lists.newArrayList();
		try
		{
			pairs.add( new Pair<String, Object>( id.toString(), toJSON().toString() ) );
		}
		catch( JSONException x )
		{
			x.printStackTrace();
		}
		return pairs;
	}


	/**
	 * schema export app.
	 * 
	 * @param args
	 *            ignored.
	 * @throws JSONException
	 */
	public static void main(String[] args) throws JSONException
	{
		LocationImpl source = new LocationImpl( "localhost", null, null, "127.0.0.1" );
		LocationImpl observer = new LocationImpl( "localhost", null, null, "127.0.0.1" );

		EventImpl measurement = new EventImpl( UUID.randomUUID(), UUID.randomUUID(), null, null, "event description string",
				new Float( 3.14f ), EventType.Measurement, ResourceType.SystemLoad, new Date().getTime(),
				new Date().getTime() + 20, source, null, observer );

		LocationImpl act_source = new LocationImpl( "localhost", "some-component-name", "some-user-id", "127.0.0.1" );
		LocationImpl act_target = new LocationImpl( "localhost", "some-component-name", "some-user-id", "127.0.0.1" );

		EventImpl action = new EventImpl( UUID.randomUUID(), UUID.randomUUID(), "some-tenant-id", "some-user-id",
				"event action name", ActionStatus.Succeded, EventType.Action, null, new Date().getTime(),
				new Date().getTime() + 20, act_source, act_target, observer );

		System.out.println( "measurement event:\n" );
		System.out.println( measurement.toJSON().toString( 2 ) );

		System.out.println( "\n\n==================================================" );
		System.out.println( "action event:\n" );
		System.out.println( action.toJSON().toString( 2 ) );
	}
}
