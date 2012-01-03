package gr.ntua.vision.monitoring.phony;

import gr.ntua.vision.monitoring.cluster.Configuration;
import gr.ntua.vision.monitoring.ext.catalog.Catalog;
import gr.ntua.vision.monitoring.ext.catalog.InMemoryLocalCatalog;
import gr.ntua.vision.monitoring.ext.catalog.LocalCatalogFactory;
import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.Event.EventType;
import gr.ntua.vision.monitoring.model.Location;
import gr.ntua.vision.monitoring.model.Resource;
import gr.ntua.vision.monitoring.model.impl.EventImpl;
import gr.ntua.vision.monitoring.model.impl.LocationImpl;
import gr.ntua.vision.monitoring.util.Pair;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.codehaus.jettison.json.JSONException;

import com.google.common.collect.Lists;


/**
 * This is used to insert phony configuration data. It will only write on an instance of {@link InMemoryLocalCatalog}, if not
 * found, it will throw an error.
 */
public abstract class PhonyConfigurationWriter {
    /** error event **/
    private static final Event error;

    static {
        Location src = null;
        try {
            final InetAddress localhost = InetAddress.getLocalHost();
            final byte[] ip = localhost.getAddress();
            src = new LocationImpl( localhost.getCanonicalHostName(), "Monitoring", null, null, String.format( "%d,%d,%d,%d",
                                                                                                               ip[0], ip[1],
                                                                                                               ip[2], ip[3] ) );
        } catch( final UnknownHostException e ) {
            e.printStackTrace();
        }
        if( src == null )
            src = new LocationImpl( "localhost", "Monitoring", null, null, "127.0.0.1" );

        error = new EventImpl( null, null, "error", EventType.Measurement, Lists.<Resource> newArrayList(), 0, 0, src, null, null );
    }


    /**
     * do the registration of false configuration data.
     */
    public static void registerPhonyConfig() {
        final Catalog catalog = LocalCatalogFactory.localCatalogInstance();
        if( !( catalog instanceof InMemoryLocalCatalog ) )
            throw new AssertionError();

        final List<Pair<String, Object>> items = Lists.newArrayList();

        items.add( new Pair<String, Object>( Configuration.CatalogKey, "vismo.actions" ) );

        final String A = "probe:local";
        items.add( new Pair<String, Object>( Configuration.ProbeNames, new String[] { A } ) );

        String errJson = "";
        try {
            errJson = error.toJSON().toString();
        } catch( final JSONException e ) {
            e.printStackTrace();
        }

        // probe A:
        items.add( new Pair<String, Object>( A + Configuration.ProbeCommandParts,
                new String[] { "/opt/vision/vismo/scripts/probe.sh" } ) );
        items.add( new Pair<String, Object>( A + Configuration.ProbeExecPeriod, 30 ) );
        items.add( new Pair<String, Object>( A + Configuration.ProbeExecTimeout, 2 ) );
        items.add( new Pair<String, Object>( A + Configuration.ProbeStoreKey, "vismo.measurements" ) );
        items.add( new Pair<String, Object>( A + Configuration.ProbeFail, errJson ) );
        items.add( new Pair<String, Object>( A + Configuration.ProbeRetries, 4 ) );
        items.add( new Pair<String, Object>( A + Configuration.ProbeInScripts, false ) );

        catalog.put( Configuration.GlobalCfgKey, items );
    }
}
