package gr.ntua.vision.monitoring.cluster;

import gr.ntua.vision.monitoring.ext.local.Catalog;
import gr.ntua.vision.monitoring.ext.local.LocalCatalogFactory;
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
public class Configuration
{
	/** the single instance. */
	static final Configuration				instance			= new Configuration();
	/** the logger. */
	@SuppressWarnings("all")
	private static final Logger				log					= Logger.getLogger( Configuration.class );

	/***/
	@SuppressWarnings("unchecked")
	private static final Class<String[]>	string_vec_t		= (Class<String[]>) new String[0].getClass();
	/** the store key for the configuration. */
	public static final String				GlobalCfgKey		= "vismo.config";
	/** the variable name for the scripts' working directory. */
	public static final String				ScriptsDir			= "vismo.scripts.dir";
	/** the variable name for the default key used for storing events in the catalog. */
	public static final String				CatalogKey			= "vismo.ctlg.key";
	/** the variable name for the names of the probes. */
	public static final String				ProbeNames			= "vismo.probes";
	/** the variable name suffix for command parts. */
	public static final String				ProbeCommandParts	= ".parts";
	/** the variable name suffix for execution period. */
	public static final String				ProbeExecPeriod		= ".period";
	/** the variable name suffix for execution timeout. */
	public static final String				ProbeExecTimeout	= ".tmout";
	/** the variable name suffix for store key name. */
	public static final String				ProbeStoreKey		= ".store";
	/** the variable name suffix for the failure response data. */
	public static final String				ProbeFail			= ".fail";
	/** the variable name suffix for retries number on fail. */
	public static final String				ProbeRetries		= ".retries";
	/** the variable name suffix for whether the script command should be prepended with the scripts folder. */
	public static final String				ProbeInScripts		= ".inscriptsdir";

	/** the set of names of configured probes. */
	private final Set<String>				liveProbes			= Sets.newHashSet();
	/** the scripts' working directory. */
	private String							scriptsWorkDir		= "/";
	/** the default catalog. */
	private String							catalogKey			= null;


	/**
	 * reload the configuration.
	 * 
	 * @param newProbes
	 *            a set where this method will place any new probes loaded.
	 * @param delete
	 *            a set where this method will place any names of probes that should be removed.
	 */
	void reload(Set<Probe> newProbes, Set<String> delete)
	{
		Catalog ctg = LocalCatalogFactory.localCatalogInstance();

		String dir = ctg.as( GlobalCfgKey, ScriptsDir, String.class );
		if( dir == null )
		{
			log.error( "Invalid configuration: null scripts working directory." );
			return;
		}
		scriptsWorkDir = dir;
		String key = ctg.as( GlobalCfgKey, CatalogKey, String.class );
		if( key == null )
		{
			log.error( "Invalid configuration: null catalog key." );
			return;
		}
		catalogKey = key;

		String[] probeNames = ctg.as( GlobalCfgKey, ProbeNames, string_vec_t );
		if( probeNames == null )
		{
			log.error( "Invalid configuration: null probe names" );
			return;
		}
		log.debug( "Live program count: " + probeNames.length );

		Set<String> live = Sets.newHashSet( liveProbes );
		liveProbes.clear();

		for( String name : probeNames )
			try
			{
				if( !live.contains( name ) )
				{
					String[] cmdparts = ctg.as( GlobalCfgKey, name + ProbeCommandParts, string_vec_t );
					if( cmdparts == null || cmdparts.length == 0 ) throw new Exception( "command parts" );
					Integer execPeriod = ctg.as( GlobalCfgKey, name + ProbeExecPeriod, Integer.class );
					if( execPeriod == null ) throw new Exception( "exec period" );
					Integer execTimeout = ctg.as( GlobalCfgKey, name + ProbeExecTimeout, Integer.class );
					if( execTimeout == null ) throw new Exception( "exec timeout" );
					String storeKey = ctg.as( GlobalCfgKey, name + ProbeStoreKey, String.class );
					if( storeKey == null ) throw new Exception( "store key" );
					String fail = ctg.as( GlobalCfgKey, name + ProbeFail, String.class );
					if( fail == null ) throw new Exception( "fail" );
					Integer retries = ctg.as( GlobalCfgKey, name + ProbeRetries, Integer.class );
					if( retries == null ) throw new Exception( "retries" );
					Boolean inScripts = ctg.as( GlobalCfgKey, name + ProbeInScripts, Boolean.class );
					if( inScripts == null ) throw new Exception( "in scripts" );

					if( inScripts )
						cmdparts[0] = ClusterMonitoring.instance.scriptsDirectory().getAbsolutePath() + File.separatorChar
								+ cmdparts[0];

					newProbes.add( ProbeFactory.create( name, cmdparts, execPeriod, execTimeout, storeKey, fail, retries ) );
				}
				liveProbes.add( name );
			}
			catch( Exception x )
			{
				log.error( "Invalid configuration for probe '" + name + "': " + x.getMessage() );
			}

		live.removeAll( liveProbes );
		delete.addAll( live );
	}


	/**
	 * get the scripts' working directory.
	 * 
	 * @return the directory.
	 */
	public String getScriptsWorkDir()
	{
		return scriptsWorkDir;
	}


	/**
	 * @return the catalogKey
	 */
	public String getCatalogKey()
	{
		return catalogKey;
	}
}
