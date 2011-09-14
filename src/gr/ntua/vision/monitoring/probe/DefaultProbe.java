package gr.ntua.vision.monitoring.probe;

import gr.ntua.vision.monitoring.cluster.ClusterMonitoring;
import gr.ntua.vision.monitoring.cluster.ProbeExecutor;
import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.Location;
import gr.ntua.vision.monitoring.model.impl.EventImpl;
import gr.ntua.vision.monitoring.model.impl.LocationImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.collect.Lists;


/**
 * The default {@link Probe} implementation.
 */
class DefaultProbe implements Probe
{
	/** the logger. */
	@SuppressWarnings("all")
	private static final Logger	log					= Logger.getLogger( DefaultProbe.class );

	/** name of the probe */
	final String				name;
	/** probe's command parts. */
	private final String[]		cmdparts;
	/** execution period. */
	private final int			execPeriod;
	/** execution timeout. */
	final int					execTimeout;
	/** local catalog storage key. */
	private final String		storeKey;
	/** the number of retries before the execution is considered failed. */
	private final int			retries;
	/** last collected event. */
	private List<Event>			last_events			= Lists.newArrayList();
	/** error event. */
	private Event				error				= null;
	/** last event collection time. */
	private long				last_collection_tm	= 0;
	/** the location of this. */
	private final Location		observer;
	/** the executor. */
	private ProbeExecutor		executor			= null;


	/**
	 * c/tor.
	 * 
	 * @param name
	 * @param cmdparts
	 * @param execPeriod
	 * @param execTimeout
	 * @param storeKey
	 * @param failResponse
	 * @param retries
	 * @throws Exception
	 *             - on creation errors.
	 */
	DefaultProbe(String name, String[] cmdparts, int execPeriod, int execTimeout, String storeKey, String failResponse,
			int retries) throws Exception
	{
		this.name = name;
		this.cmdparts = cmdparts;
		this.execPeriod = execPeriod;
		this.execTimeout = execTimeout;
		this.storeKey = storeKey;
		this.retries = retries;

		this.error = failResponse != null && failResponse.length() > 0 ? new EventImpl( new JSONObject( failResponse ) ) : null;

		InetAddress localhost = InetAddress.getLocalHost();
		byte[] ip = localhost.getAddress();
		this.observer = new LocationImpl( localhost.getCanonicalHostName(), "Monitoring", null, null,
				String.format( "%d,%d,%d,%d", ip[0], ip[1], ip[2], ip[3] ) );
	}


	/**
	 * @see gr.ntua.vision.monitoring.probe.Probe#setExecutor(gr.ntua.vision.monitoring.cluster.ProbeExecutor)
	 */
	@Override
	public void setExecutor(ProbeExecutor executor)
	{
		this.executor = executor;
	}


	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		String jsonStr = null;
		log.debug( "Executing" );
		last_events.clear();
		last_collection_tm = new Date().getTime();

		for( int tries = 0; tries < retries; ++tries )
			try
			{
				log.debug( "run(), attempt: " + ( tries + 1 ) + "/" + retries );
				Process proc = Runtime.getRuntime().exec( cmdparts );
				consumeStream( proc.getErrorStream() );

				StringBuilder buf = new StringBuilder();
				BufferedReader input = new BufferedReader( new InputStreamReader( proc.getInputStream() ) );
				String line = null;
				while( ( line = input.readLine() ) != null )
				{
					buf.append( line );
					buf.append( '\n' );
				}
				input.close();

				if( proc.waitFor() != 0 )
				{
					log.debug( "failed (bad exit code:" + proc.exitValue() + ") @ attempt: " + ( tries + 1 ) + "/" + retries );
					continue;
				}

				jsonStr = buf.toString();

				last_collection_tm = new Date().getTime();
				JSONArray events = new JSONArray( jsonStr );
				for( int i = 0; i < events.length(); ++i )
					last_events.add( new EventImpl( events.getJSONObject( i ) ).setObserver( observer ) );

				break;
			}
			catch( IOException x )
			{
				if( x.getMessage().contains( "Permission denied" ) && executor != null )
				{
					log.info( "Permission denied in running probe: " + name + " :: removing probe" );
					ClusterMonitoring.instance.remove( executor );
					return;
				}
				log.debug( "failed (I/O error:" + x.getMessage() + ") @ attempt: " + ( tries + 1 ) + "/" + retries );
				if( tries == retries - 1 ) pushErrorEvent( x );
			}
			catch( Exception x )
			{
				log.warn( "failed (" + x.getMessage() + ") @ attempt: " + ( tries + 1 ) + "/" + retries );
				if( tries == retries - 1 ) pushErrorEvent( x );
			}

		log.debug( "Done" );
	}


	/**
	 * push an error event.
	 * 
	 * @param x
	 */
	private void pushErrorEvent(Exception x)
	{
		EventImpl err = new EventImpl( error );
		err.setDescription( x.getClass().getCanonicalName() + " :: " + x.getMessage() ).setObserver( observer )
				.setTime( new Date().getTime() );
		last_events.clear();
		last_events.add( err );
	}


	/**
	 * @see gr.ntua.vision.monitoring.probe.Probe#name()
	 */
	@Override
	public String name()
	{
		return name;
	}


	/**
	 * @see gr.ntua.vision.monitoring.probe.Probe#storeKey()
	 */
	@Override
	public String storeKey()
	{
		return storeKey;
	}


	/**
	 * @see gr.ntua.vision.monitoring.probe.Probe#period()
	 */
	@Override
	public int period()
	{
		return execPeriod;
	}


	/**
	 * @see gr.ntua.vision.monitoring.probe.Probe#lastCollectionTime()
	 */
	@Override
	public long lastCollectionTime()
	{
		return last_collection_tm;
	}


	/**
	 * @see gr.ntua.vision.monitoring.probe.Probe#lastCollected()
	 */
	@Override
	public List<Event> lastCollected()
	{
		return last_events;
	}


	/**
	 * set a watchdog, if required, for the given process.
	 * 
	 * @param proc
	 *            the process.
	 */
	protected void setWatchdog(final Process proc)
	{
		if( execTimeout <= 0 ) return;

		new Thread( "Wathdog::" + name ) {
			@Override
			public void run()
			{
				setDaemon( true );
				try
				{
					Thread.sleep( execTimeout * 1000 );
					proc.destroy();
				}
				catch( Throwable x )
				{
					// ignore any possible error.
				}
			}
		}.start();
	}


	/**
	 * consume the stream given.
	 * 
	 * @param stream
	 *            the stream.
	 */
	protected void consumeStream(final InputStream stream)
	{
		new Thread( "StreamConsumer::" + name ) {
			@Override
			public void run()
			{
				try
				{
					while( stream.read() >= 0 )
					{
						// NOP
					}
				}
				catch( Throwable x )
				{
					// ignore any possible error.
				}
			}
		}.start();
	}
}
