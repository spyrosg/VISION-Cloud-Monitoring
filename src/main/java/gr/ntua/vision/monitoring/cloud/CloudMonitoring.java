package gr.ntua.vision.monitoring.cloud;

import gr.ntua.vision.monitoring.Monitoring;
import gr.ntua.vision.monitoring.ext.catalog.LocalCatalogFactory;
import gr.ntua.vision.monitoring.rules.EventReader;
import gr.ntua.vision.monitoring.rules.RuleEngine;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;


/**
 * This is the application singleton object.
 */
public class CloudMonitoring implements Monitoring {
    /** single instance. */
    public static final CloudMonitoring instance    = new CloudMonitoring();
    /** the logger. */
    @SuppressWarnings("all")
    private static final Logger         log         = Logger.getLogger( CloudMonitoring.class );
    /** the rule engine */
    public final RuleEngine             ruleEngine  = new RuleEngine();
    /** the event reader. */
    private EventReader                 eventReader = null;


    /**
     * c/tor.
     */
    private CloudMonitoring() {
        // nop
    }


    /**
     * @see gr.ntua.vision.monitoring.Monitoring#isInstanceAlive()
     */
    @Override
    public boolean isInstanceAlive() {
        return ruleEngine.isAlive();
    }


    /**
     * @see gr.ntua.vision.monitoring.Monitoring#launch(javax.servlet.ServletContext)
     */
    @Override
    public void launch(final ServletContext ctx) {
        log.info( "Application begins" );
        eventReader = new EventReader( ruleEngine );
        setClusters( LocalCatalogFactory.getLocalURL() );
        eventReader.start();
        ruleEngine.start();
    }


    /**
     * set the clusters to read from.
     * 
     * @param clusters
     *            the clusters.
     */
    public void setClusters(final String... clusters) {
        eventReader.clear();
        for( final String cluster : clusters ) {
            eventReader.register( cluster, "vismo.measurements" );
            eventReader.register( cluster, "vismo.actions" );
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.Monitoring#shutdown()
     */
    @Override
    public void shutdown() {
        log.info( "Application stops" );
        try {
            ruleEngine.shutdown();
        } catch( final InterruptedException x ) {
            x.printStackTrace();
        }
        if( eventReader != null )
            eventReader.interrupt();
    }
}
