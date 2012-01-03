package gr.ntua.vision.monitoring.cluster;

import gr.ntua.vision.monitoring.ext.catalog.Catalog;
import gr.ntua.vision.monitoring.ext.catalog.LocalCatalogFactory;
import gr.ntua.vision.monitoring.probe.Probe;
import gr.ntua.vision.monitoring.probe.ProbeFactory;

import java.io.File;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;


/**
 * This manages the monitoring application configuration. It is a singleton object, with its instance accessible only inside the
 * package. {@link ClusterMonitoring} singleton should be responsible for distributing it outside.
 */
public class Configuration {
    /** the variable name for the default key used for storing events in the catalog. */
    public static final String           CatalogKey        = "vismo.ctlg.key";
    /** the store key for the configuration. */
    public static final String           GlobalCfgKey      = "vismo.config";

    /** the variable name suffix for command parts. */
    public static final String           ProbeCommandParts = ".parts";
    /** the variable name suffix for execution period. */
    public static final String           ProbeExecPeriod   = ".period";
    /** the variable name suffix for execution timeout. */
    public static final String           ProbeExecTimeout  = ".tmout";
    /** the variable name suffix for the failure response data. */
    public static final String           ProbeFail         = ".fail";
    /** the variable name suffix for whether the script command should be prepended with the scripts folder. */
    public static final String           ProbeInScripts    = ".inscriptsdir";
    /** the variable name for the names of the probes. */
    public static final String           ProbeNames        = "vismo.probes";
    /** the variable name suffix for retries number on fail. */
    public static final String           ProbeRetries      = ".retries";
    /** the variable name suffix for store key name. */
    public static final String           ProbeStoreKey     = ".store";
    /** the single instance. */
    static final Configuration           instance          = new Configuration();
    /** the logger. */
    @SuppressWarnings("all")
    private static final Logger          log               = Logger.getLogger( Configuration.class );
    /***/
    @SuppressWarnings("unchecked")
    private static final Class<String[]> string_vec_t      = (Class<String[]>) new String[0].getClass();

    /** the default catalog. */
    private String                       catalogKey        = null;
    /** the set of names of configured probes. */
    private final Set<String>            liveProbes        = Sets.newHashSet();


    /**
     * @return the catalogKey
     */
    public String getCatalogKey() {
        return catalogKey;
    }


    /**
     * reload the configuration.
     * 
     * @param newProbes
     *            a set where this method will place any new probes loaded.
     * @param delete
     *            a set where this method will place any names of probes that should be removed.
     */
    void reload(final Set<Probe> newProbes, final Set<String> delete) {
        final Catalog ctg = LocalCatalogFactory.localCatalogInstance();

        final String key = ctg.as( GlobalCfgKey, CatalogKey, String.class );
        if( key == null ) {
            log.error( "Invalid configuration: null catalog key." );
            return;
        }
        catalogKey = key;

        final String[] probeNames = ctg.as( GlobalCfgKey, ProbeNames, string_vec_t );
        if( probeNames == null ) {
            log.error( "Invalid configuration: null probe names" );
            return;
        }
        log.debug( "Live program count: " + probeNames.length );

        final Set<String> live = Sets.newHashSet( liveProbes );
        liveProbes.clear();

        for( final String name : probeNames )
            try {
                if( !live.contains( name ) ) {
                    final String[] cmdparts = ctg.as( GlobalCfgKey, name + ProbeCommandParts, string_vec_t );
                    if( cmdparts == null || cmdparts.length == 0 )
                        throw new Exception( "command parts" );
                    final Integer execPeriod = ctg.as( GlobalCfgKey, name + ProbeExecPeriod, Integer.class );
                    if( execPeriod == null )
                        throw new Exception( "exec period" );
                    final Integer execTimeout = ctg.as( GlobalCfgKey, name + ProbeExecTimeout, Integer.class );
                    if( execTimeout == null )
                        throw new Exception( "exec timeout" );
                    final String storeKey = ctg.as( GlobalCfgKey, name + ProbeStoreKey, String.class );
                    if( storeKey == null )
                        throw new Exception( "store key" );
                    final String fail = ctg.as( GlobalCfgKey, name + ProbeFail, String.class );
                    if( fail == null )
                        throw new Exception( "fail" );
                    final Integer retries = ctg.as( GlobalCfgKey, name + ProbeRetries, Integer.class );
                    if( retries == null )
                        throw new Exception( "retries" );
                    final Boolean inScripts = ctg.as( GlobalCfgKey, name + ProbeInScripts, Boolean.class );
                    if( inScripts == null )
                        throw new Exception( "in scripts" );

                    if( inScripts )
                        cmdparts[0] = ClusterMonitoring.instance.scriptsDirectory().getAbsolutePath() + File.separatorChar
                                + cmdparts[0];

                    newProbes.add( ProbeFactory.create( name, cmdparts, execPeriod, execTimeout, storeKey, fail, retries ) );
                }
                liveProbes.add( name );
            } catch( final Exception x ) {
                log.error( "Invalid configuration for probe '" + name + "': " + x.getMessage() );
            }

        live.removeAll( liveProbes );
        delete.addAll( live );
    }
}
