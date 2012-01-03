package gr.ntua.vision.monitoring.cluster;

import gr.ntua.vision.monitoring.ext.catalog.LocalCatalogFactory;
import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.probe.Probe;
import gr.ntua.vision.monitoring.util.Pair;

import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * This encapsulates a probe and its execution mechanism.
 */
public class ProbeExecutor {
    /** the logger. */
    @SuppressWarnings("all")
    private static final Logger log     = Logger.getLogger( ProbeExecutor.class );
    /** the probe this executes. */
    public final Probe          probe;
    /** the current executing thread, if any, otherwise <code>null</code> */
    private Thread              current = null;
    /** the internal timer. It counts seconds since last execution. */
    private int                 timer;


    /**
     * c/tor.
     * 
     * @param probe
     */
    ProbeExecutor(final Probe probe) {
        this.probe = probe;
        probe.setExecutor( this );

        // ensure the probe will run on first tick.
        this.timer = probe.period() - 1;
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
        final ProbeExecutor other = (ProbeExecutor) obj;
        if( probe == null ) {
            if( other.probe != null )
                return false;
        } else if( !probe.equals( other.probe ) )
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
        result = prime * result + ( ( probe == null ) ? 0 : probe.hashCode() );
        return result;
    }


    /**
     * notify this executor one second passed since last time this method was called.
     * <p>
     * IMPORTANT: this has no effect until the last executing probe is done with its job.
     * </p>
     */
    public void tick() {
        if( current != null )
            return;

        ++timer;

        if( timer >= probe.period() ) {
            timer = 0;

            final Runnable r = new Runnable() {
                @SuppressWarnings("synthetic-access")
                @Override
                public void run() {
                    try {
                        probe.run();

                        // save the stuff:
                        final long tmstamp = probe.lastCollectionTime();
                        final List<Event> events = probe.lastCollected();

                        saveEvents( tmstamp, events, probe.storeKey() );
                        log.debug( "Event saved" );
                    } finally {
                        current = null;
                    }
                }
            };

            current = new Thread( r, probe.name() );
            current.setDaemon( true );
            current.start();
        }
    }


    /**
     * save the events given to the catalog service.
     * 
     * @param tmstamp
     * @param events
     * @param key
     */
    public static void saveEvents(final long tmstamp, final List<Event> events, final String key) {
        final List<Pair<String, Object>> items = Lists.newArrayList();
        Iterables.addAll( items, Iterables.transform( events, new Function<Event, Pair<String, Object>>() {
            @Override
            public Pair<String, Object> apply(final Event event) {
                try {
                    return new Pair<String, Object>( event.id().toString(), event.toJSON().toString() );
                } catch( final JSONException e ) {
                    e.printStackTrace();
                    return null;
                }
            }
        } ) );
        Iterables.removeIf( items, Predicates.isNull() );

        LocalCatalogFactory.localCatalogInstance().put( key == null ? Configuration.instance.getCatalogKey() : key, tmstamp,
                                                        items );
    }
}
