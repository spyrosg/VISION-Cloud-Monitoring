package gr.ntua.vision.monitoring.model.impl;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.Location;
import gr.ntua.vision.monitoring.model.Resource;
import gr.ntua.vision.monitoring.util.Pair;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * Base implementation of the event interface.
 */
public class EventImpl implements Event
{
	/** event id. */
	private final UUID		id;
	/** probe id. */
	private final UUID		probe;
	/** event description. */
	private String			description;
	/** event's type. */
	private final EventType	type;
	/** event's resources. */
	private List<Resource>	resources;
	/** begin time of the event. */
	private final long		start;
	/** end time of the event. */
	private final long		end;
	/** event's source. */
	private final Location	source;
	/** event's target. */
	private final Location	target;
	/** event's observer. */
	private Location		observer;
	/** the aggregation count. */
	private int				aggregation_count;


	/**
	 * c/tor.
	 * 
	 * @param id
	 * @param probe
	 * @param description
	 * @param type
	 * @param resources
	 * @param start
	 * @param end
	 * @param source
	 * @param target
	 * @param observer
	 */
	public EventImpl(UUID id, UUID probe, String description, EventType type, List<Resource> resources, long start, long end,
			Location source, Location target, Location observer)
	{
		this.id = id;
		this.probe = probe;
		this.description = description;
		this.type = type;
		this.resources = resources;
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
		this.description = event.getDescription();
		this.type = event.eventType();
		this.resources = Lists.newArrayList( event.resources() );
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
		String json_id = json.optString( "id" );
		id = json_id == null ? UUID.randomUUID() : UUID.fromString( json_id );
		probe = UUID.fromString( json.getString( "probe" ) );
		String desc = json.optString( "description" );
		description = desc == null ? "" : desc;
		type = EventType.valueOf( json.getString( "type" ) );
		start = json.getLong( "start" );
		end = json.getLong( "end" );
		aggregation_count = json.optInt( "aggr_count" );

		JSONArray rsc = json.getJSONArray( "resources" );
		resources = Lists.newArrayList();
		for( int i = 0; i < rsc.length(); ++i )
			resources.add( new ResourceImpl( rsc.getJSONObject( i ) ) );

		JSONObject tmp = json.getJSONObject( "source" );
		source = new LocationImpl( tmp );
		tmp = json.optJSONObject( "target" );
		target = tmp == null ? null : new LocationImpl( tmp );
		tmp = json.optJSONObject( "observer" );
		observer = tmp == null ? null : new LocationImpl( tmp );
	}


	/**
	 * merge this with the given event.
	 * 
	 * @param event
	 *            the event to merge with.
	 */
	public void mergeWith(Event event)
	{
		List<Resource> all = ResourceImpl.merge( resources, event.resources() );
		resources = all;

		++aggregation_count;
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
		obj.put( "description", description );
		obj.put( "type", type.toString() );
		obj.put( "start", start );
		obj.put( "end", end );

		Iterable<JSONObject> rscs = Iterables.transform( resources, new Function<Resource, JSONObject>() {
			@Override
			public JSONObject apply(Resource rsc)
			{
				try
				{
					return rsc.toJSON();
				}
				catch( JSONException e )
				{
					e.printStackTrace();
					return null;
				}
			}
		} );
		Iterables.removeIf( rscs, Predicates.isNull() );
		JSONArray rsc = new JSONArray( Lists.newArrayList( rscs ) );
		obj.put( "resource", rsc );
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
	 * @see gr.ntua.vision.monitoring.model.Event#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return description;
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
	 * @see gr.ntua.vision.monitoring.model.Event#resources()
	 */
	@Override
	public List<Resource> resources()
	{
		return resources;
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
	 * @see gr.ntua.vision.monitoring.model.Event#setObserver(gr.ntua.vision.monitoring.model.Location)
	 */
	@Override
	public Event setObserver(Location observer)
	{
		this.observer = observer;
		return this;
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
	 * @param description
	 *            the description to set
	 * @return <code>this</code>
	 */
	public EventImpl setDescription(String description)
	{
		this.description = description;
		return this;
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
		LocationImpl source = new LocationImpl( "localhost", null, null, null, "127.0.0.1" );
		LocationImpl observer = new LocationImpl( "localhost", null, null, null, "127.0.0.1" );

		List<Resource> resources = Arrays.<Resource> asList(	new ResourceImpl( "execution-time", "seconds", 20.34 ),
																new ResourceImpl( "memory", "GB", 2.34 ), new ResourceImpl(
																		"storage", "GB", 200.34 ) );

		EventImpl measurement = new EventImpl( UUID.randomUUID(), UUID.randomUUID(), "event description string",
				EventType.Measurement, resources, new Date().getTime(), new Date().getTime() + 20, source, null, observer );

		LocationImpl act_source = new LocationImpl( "localhost", "some-component-name", "some-user-id", "some-tenant-id",
				"127.0.0.1" );
		LocationImpl act_target = new LocationImpl( "localhost", "some-component-name", "some-user-id", "some-tenant-id",
				"127.0.0.1" );

		EventImpl action = new EventImpl( UUID.randomUUID(), UUID.randomUUID(), "event action name", EventType.Action, resources,
				new Date().getTime(), new Date().getTime() + 20, act_source, act_target, observer );

		System.out.println( "measurement event:\n" );
		System.out.println( measurement.toJSON().toString( 2 ) );

		System.out.println( "\n\n==================================================" );
		System.out.println( "action event:\n" );
		System.out.println( action.toJSON().toString( 2 ) );
	}
}
