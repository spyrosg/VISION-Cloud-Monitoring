package gr.ntua.vision.monitoring.model.impl;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.Location;
import gr.ntua.vision.monitoring.model.Resource;
import gr.ntua.vision.monitoring.util.Pair;

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
	 * @see gr.ntua.vision.monitoring.model.Event#serialize()
	 */
	@Override
	public List<Pair<String, Object>> serialize()
	{
		List<Pair<String, Object>> pairs = Lists.newArrayList();

		pairs.add( new Pair<String, Object>( "id", id ) );
		pairs.add( new Pair<String, Object>( "probe", probe ) );
		pairs.add( new Pair<String, Object>( "tenant", tenant ) );
		pairs.add( new Pair<String, Object>( "user", user ) );
		pairs.add( new Pair<String, Object>( "description", description ) );
		pairs.add( new Pair<String, Object>( "type", type.toString() ) );
		pairs.add( new Pair<String, Object>( "startTm", start ) );
		pairs.add( new Pair<String, Object>( "endTm", end ) );
		pairs.add( new Pair<String, Object>( "resource", resource.toString() ) );
		pairs.add( new Pair<String, Object>( "value", value.toString() ) );
		pairs.add( new Pair<String, Object>( "aggregationCount", aggregation_count ) );
		pairs.add( new Pair<String, Object>( "source.hostname", source.hostname() ) );
		pairs.add( new Pair<String, Object>( "source.service", source.service() ) );
		pairs.add( new Pair<String, Object>( "source.userID", source.userID() ) );
		pairs.add( new Pair<String, Object>( "source.netAddress", source.netAddress() ) );
		pairs.add( new Pair<String, Object>( "target.hostname", target.hostname() ) );
		pairs.add( new Pair<String, Object>( "target.service", target.service() ) );
		pairs.add( new Pair<String, Object>( "target.userID", target.userID() ) );
		pairs.add( new Pair<String, Object>( "target.netAddress", target.netAddress() ) );
		pairs.add( new Pair<String, Object>( "observer.hostname", observer.hostname() ) );
		pairs.add( new Pair<String, Object>( "observer.service", observer.service() ) );
		pairs.add( new Pair<String, Object>( "observer.userID", observer.userID() ) );
		pairs.add( new Pair<String, Object>( "observer.netAddress", observer.netAddress() ) );

		return pairs;
	}
}
