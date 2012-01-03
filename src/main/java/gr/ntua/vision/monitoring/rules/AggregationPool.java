package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.cloud.CloudMonitoring;
import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.impl.EventImpl;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Maps;


/**
 * An event aggregation pool. Pools aggregate events and create more compact ones. They are responsible for performing the action
 * on the aggregated event when the conditions permit so.
 */
public class AggregationPool extends Thread {
    /** an aggregated event key. */
    private class Key {
        /** creation timestamp. */
        final long     timestamp;
        /** the values of the key fields. */
        final Object[] values;


        /**
         * c/tor.
         * 
         * @param event
         */
        Key(final Event event) {
            this.values = new Object[keys.length];
            for( int i = 0; i < values.length; ++i )
                values[i] = keys[i].fieldValue( event );

            this.timestamp = timeWindow > 0 ? new Date().getTime() : 0;
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
            final Key other = (Key) obj;
            if( !Arrays.equals( values, other.values ) )
                return false;
            return true;
        }


        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode( values );
            return result;
        }
    }

    /** the logger. */
    @SuppressWarnings("all")
    private static final Logger            log        = Logger.getLogger( AggregationPool.class );
    /** the pool's id. */
    public final UUID                      id;
    /** the aggregation keys. */
    final CheckedField[]                   keys;
    /** the maximum time difference between the most recent and the oldest event in the group. */
    final long                             timeWindow;
    /** the action to perform with aggregated events. */
    private final Function<Event, Boolean> action;
    /** the aggregated events. */
    private final Map<Key, EventImpl>      aggregated = Maps.newConcurrentMap();
    /** the maximum count of events in a group. */
    private final int                      maxCount;


    /**
     * c/tor.
     * 
     * @param id
     * @param maxCount
     * @param timeWindow
     * @param keys
     * @param action
     */
    public AggregationPool(final UUID id, final int maxCount, final long timeWindow, final CheckedField[] keys,
            final Function<Event, Boolean> action) {
        if( keys.length == 0 )
            throw new IllegalArgumentException( "at least one aggregation field is required." );

        this.id = id;
        this.maxCount = maxCount;
        this.timeWindow = timeWindow;
        this.keys = keys;
        this.action = action;

        log.info( "Created pool: " + id + " over: " + Arrays.toString( keys ) );

        setName( "AggregationPool[" + id + "]:Scheduler" );
        start();
    }


    /**
     * push the given event in this aggregation pool.
     * 
     * @param event
     *            the event to push.
     * @return <code>true</code> if and only if the operation completed successfully.
     */
    public boolean push(final Event event) {
        final Key key = new Key( event );
        EventImpl master = aggregated.get( key );

        if( master != null )
            master.mergeWith( event );
        else
            aggregated.put( key, master = new EventImpl( event ) );

        return true;
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        log.debug( id + " :: committer starts" );
        while( true )
            try {
                Thread.sleep( 500 );

                final long now = timeWindow > 0 ? new Date().getTime() : 0;

                final Iterator<Map.Entry<Key, EventImpl>> events = aggregated.entrySet().iterator();
                while( events.hasNext() ) {
                    final Map.Entry<Key, EventImpl> entry = events.next();

                    if( ( maxCount > 0 && entry.getValue().aggregationCount() >= maxCount ) || //
                            ( timeWindow > 0 && now - entry.getKey().timestamp >= timeWindow ) ) {
                        if( !action.apply( entry.getValue() ) ) //
                            CloudMonitoring.instance.ruleEngine.remove( id );
                        events.remove();
                    }
                }
            } catch( final InterruptedException x ) {
                log.debug( id + " :: committer stops" );
                return;
            } catch( final Throwable x ) {
                // ignore
                x.printStackTrace();
            }
    }


    /**
     * destroy this aggregation pool.
     * 
     * @throws InterruptedException
     */
    public void shutdown() throws InterruptedException {
        log.info( "shutdown" );
        interrupt();
        join();
    }
}
