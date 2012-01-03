package gr.ntua.vision.monitoring.cluster;

import gr.ntua.vision.monitoring.Monitoring;
import gr.ntua.vision.monitoring.ext.catalog.LocalCatalogFactory;
import gr.ntua.vision.monitoring.phony.PhonyConfigurationWriter;
import gr.ntua.vision.monitoring.probe.Probe;

import java.io.File;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;


/**
 * This is the application singleton object.
 */
public class ClusterMonitoring extends Scheduler implements Monitoring
{
	/** single instance. */
	public static final ClusterMonitoring	instance	= new ClusterMonitoring();
	/** the logger. */
	@SuppressWarnings("all")
	private static final Logger				log			= Logger.getLogger( ClusterMonitoring.class );
	/** the real application path */
	private String							realPath	= "/";
	/** scheduler thread */
	private Thread							scheduler	= null;


	/**
	 * c/tor.
	 */
	private ClusterMonitoring()
	{
		// TODO: remove this!
		PhonyConfigurationWriter.registerPhonyConfig();
	}


	/**
	 * get the configuration instance.
	 * 
	 * @return the configuration instance in use.
	 */
	public Configuration configuration()
	{
		return Configuration.instance;
	}


	/**
	 * @see gr.ntua.vision.monitoring.Monitoring#launch(javax.servlet.ServletContext)
	 */
	@Override
	public void launch(ServletContext ctx)
	{
		log.info( "Application begins" );

		this.realPath = ctx.getRealPath( "/" );

		reConfigure();
		// XXX: hack to force read config from in-memory catalog.
		LocalCatalogFactory.setLocalURL( LocalCatalogFactory.getLocalURL() );

		scheduler = new Thread( this );
		scheduler.setName( "Scheduler" );
		scheduler.setDaemon( true );
		scheduler.start();
	}


	/**
	 * @see gr.ntua.vision.monitoring.Monitoring#isInstanceAlive()
	 */
	@Override
	public boolean isInstanceAlive()
	{
		return scheduler != null && scheduler.isAlive();
	}


	/**
	 * get the scripts working directory.
	 * 
	 * @return the directory.
	 */
	public File scriptsDirectory()
	{
		return new File( realPath + File.separatorChar + "scripts" );
	}


	/**
	 * reconfigure the application & update the live probe lists.
	 */
	public void reConfigure()
	{
		final Set<Probe> probes = Sets.newHashSet(); // just the new ones.
		final Set<String> delete = Sets.newHashSet(); // names of those to delete.

		Configuration.instance.reload( probes, delete );

		log.info( "Refreshing probe list: size=" + this.probes.size() + " will add:" + probes.size() + " will delete:"
				+ delete.size() );
		synchronized( this.probes )
		{
			Iterables.removeIf( this.probes, new Predicate<ProbeExecutor>() {
				@Override
				public boolean apply(ProbeExecutor e)
				{
					return delete.contains( e.probe.name() );
				}
			} );

			for( Probe p : probes )
				this.probes.add( new ProbeExecutor( p ) );
		}
		log.debug( "done, probe list size: " + this.probes.size() );
	}


	/**
	 * @see gr.ntua.vision.monitoring.Monitoring#shutdown()
	 */
	@Override
	public void shutdown()
	{
		log.info( "Application stops" );
		scheduler.interrupt();
		scheduler = null;
	}
}
