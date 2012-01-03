package gr.ntua.vision.monitoring.model.impl;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.Location;
import gr.ntua.vision.monitoring.model.Resource;
import gr.ntua.vision.monitoring.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * Base implementation of the event interface.
 */
public class EventImpl implements Event {
    /** the aggregation count. */
    private int                  aggregation_count;
    /** event description. */
    private String               description;
    /** end time of the event. */
    private long                 end;
    /** event id. */
    private final UUID           id;
    /** event's observer. */
    private Location             observer;
    /** probe id. */
    private final UUID           probe;
    /** event's resources. */
    private final List<Resource> resources = Lists.newArrayList();
    /** event's source. */
    private final Location       source;
    /** begin time of the event. */
    private long                 start;
    /** event's target. */
    private final Location       target;
    /** event's type. */
    private final EventType      type;


    /**
     * copy c/tor.
     * 
     * @param event
     */
    public EventImpl(final Event event) {
        this.id = event.id();
        this.probe = event.probeID();
        this.description = event.getDescription();
        this.type = event.eventType();
        this.resources.addAll( event.resources() );
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
    public EventImpl(final JSONObject json) throws JSONException {
        final String json_id = json.optString( "id" );
        id = json_id.length() == 0 ? UUID.randomUUID() : UUID.fromString( json_id );

        probe = UUID.fromString( json.getString( "probe" ) );
        description = json.optString( "description" );
        type = EventType.valueOf( json.getString( "type" ) );
        start = json.getLong( "start" );
        end = json.getLong( "end" );
        aggregation_count = json.optInt( "aggr_count" );

        final JSONArray rsc = json.getJSONArray( "resources" );
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
    public EventImpl(final UUID id, final UUID probe, final String description, final EventType type,
            final List<Resource> resources, final long start, final long end, final Location source, final Location target,
            final Location observer) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.probe = probe == null ? new UUID( 0, 0 ) : probe;
        this.description = description;
        this.type = type;
        this.resources.addAll( resources );
        this.start = start;
        this.end = end;
        this.source = source;
        this.target = target;
        this.observer = observer;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#aggregationCount()
     */
    @Override
    public int aggregationCount() {
        return aggregation_count;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#endTime()
     */
    @Override
    public long endTime() {
        return end;
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if( this == obj )
            return true;
        if( obj == null )
            return false;
        if( getClass() != obj.getClass() )
            return false;
        final EventImpl other = (EventImpl) obj;
        if( id == null ) {
            if( other.id != null )
                return false;
        } else if( !id.equals( other.id ) )
            return false;
        return true;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#eventType()
     */
    @Override
    public EventType eventType() {
        return type;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
        return result;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#id()
     */
    @Override
    public UUID id() {
        return id;
    }


    /**
     * merge this with the given event.
     * 
     * @param event
     *            the event to merge with.
     */
    public void mergeWith(final Event event) {
        resources.clear();
        resources.addAll( ResourceImpl.merge( resources, event.resources() ) );

        ++aggregation_count;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#observer()
     */
    @Override
    public Location observer() {
        return observer;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#probeID()
     */
    @Override
    public UUID probeID() {
        return probe;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#resources()
     */
    @Override
    public List<Resource> resources() {
        return resources;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#serialize()
     */
    @Override
    public List<Pair<String, Object>> serialize() {
        final List<Pair<String, Object>> pairs = Lists.newArrayList();
        try {
            pairs.add( new Pair<String, Object>( id.toString(), toJSON().toString() ) );
        } catch( final JSONException x ) {
            x.printStackTrace();
        }
        return pairs;
    }


    /**
     * @param description
     *            the description to set
     * @return <code>this</code>
     */
    public EventImpl setDescription(final String description) {
        this.description = description;
        return this;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#setObserver(gr.ntua.vision.monitoring.model.Location)
     */
    @Override
    public Event setObserver(final Location observer) {
        this.observer = observer;
        return this;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#setTime(long)
     */
    @Override
    public void setTime(final long tm) {
        start = end = tm;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#source()
     */
    @Override
    public Location source() {
        return source;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#startTime()
     */
    @Override
    public long startTime() {
        return start;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.Event#target()
     */
    @Override
    public Location target() {
        return target;
    }


    /**
     * @see gr.ntua.vision.monitoring.model.JSONExchanged#toJSON()
     */
    @Override
    public JSONObject toJSON() throws JSONException {
        final JSONObject obj = new JSONObject();

        obj.put( "id", id.toString() );
        obj.put( "probe", probe.toString() );
        obj.put( "description", description );
        obj.put( "type", type.toString() );
        obj.put( "start", start );
        obj.put( "end", end );

        final Iterable<JSONObject> rscs = Iterables.transform( resources, new Function<Resource, JSONObject>() {
            @Override
            public JSONObject apply(final Resource rsc) {
                try {
                    return rsc.toJSON();
                } catch( final JSONException e ) {
                    e.printStackTrace();
                    return null;
                }
            }
        } );
        Iterables.removeIf( rscs, Predicates.isNull() );
        final JSONArray rsc = new JSONArray( Lists.newArrayList( rscs ) );
        obj.put( "resources", rsc );
        obj.put( "aggr_count", aggregation_count );
        obj.put( "source", source.toJSON() );
        obj.put( "target", target == null ? null : target.toJSON() );
        obj.put( "observer", observer == null ? null : observer.toJSON() );

        return obj;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append( "EventImpl [id=" );
        builder.append( id );
        builder.append( ", probe=" );
        builder.append( probe );
        builder.append( ", description=" );
        builder.append( description );
        builder.append( ", type=" );
        builder.append( type );
        builder.append( ", resources=" );
        builder.append( Arrays.toString( resources.toArray() ) );
        builder.append( ", start=" );
        builder.append( start );
        builder.append( ", end=" );
        builder.append( end );
        builder.append( ", source=" );
        builder.append( source );
        builder.append( ", target=" );
        builder.append( target );
        builder.append( ", observer=" );
        builder.append( observer );
        builder.append( ", aggregation_count=" );
        builder.append( aggregation_count );
        builder.append( "]" );
        return builder.toString();
    }
}
