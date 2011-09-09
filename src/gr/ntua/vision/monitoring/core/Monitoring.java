package gr.ntua.vision.monitoring.core;

import gr.ntua.vision.monitoring.phony.PhonyConfigurationWriter;
import gr.ntua.vision.monitoring.probe.Probe;

import java.io.File;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;


/**
 * This is the application singleton object.
 */
public class Monitoring extends Scheduler
{
	/** single instance. */
	public static final Monitoring	instance	= new Monitoring();
	/** the logger. */
	@SuppressWarnings("all")
	private static final Logger		log			= Logger.getLogger( Monitoring.class );
	/** the real application path */
	private String					realPath	= "/";


	/**
	 * c/tor.
	 */
	private Monitoring()
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
	 * launch the application. It is illegal to call this more than once, even if {@link #shutdown()} has been called in the
	 * between two successive invocations.
	 * 
	 * @param realPath
	 *            the path where the application is installed in.
	 */
	public void launch(String realPath)
	{
		log.info( "Application begins" );

		this.realPath = realPath;

		reConfigure();
		start();
	}


	/**
	 * get the scripts working directory.
	 * 
	 * @return the directory.
	 */
	public File scriptsDirectory()
	{
		if( Configuration.instance.getScriptsWorkDir().startsWith( "/" ) )
			return new File( Configuration.instance.getScriptsWorkDir() );

		return new File( realPath + File.separatorChar + Configuration.instance.getScriptsWorkDir() );
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
	 * shutdown the application. It is illegal to call this more than once.
	 */
	public void shutdown()
	{
		log.info( "Application stops" );
		interrupt();
	}
}
