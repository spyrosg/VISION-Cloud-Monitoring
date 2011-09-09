package gr.ntua.vision.monitoring.probe;

import gr.ntua.vision.monitoring.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;


/**
 * The default {@link Probe} implementation.
 */
class DefaultProbe implements Probe
{
	/** the logger. */
	@SuppressWarnings("all")
	private static final Logger					log					= Logger.getLogger( DefaultProbe.class );

	/** name of the probe */
	final String								name;
	/** probe's command parts. */
	private final String[]						cmdparts;
	/** attribute list elements separator. */
	private final char							attrSep;
	/** key value separator. */
	private final char							kvSep;
	/** execution period. */
	private final int							execPeriod;
	/** execution timeout. */
	final int									execTimeout;
	/** local catalog storage key. */
	private final String						storeKey;
	/** the number of retries before the execution is considered failed. */
	private final int							retries;

	/** last collected event. */
	private final List<Pair<String, Object>>	last_event			= Lists.newArrayList();
	/** error event. */
	private final List<Pair<String, Object>>	error				= Lists.newArrayList();
	/** last event collection time. */
	private long								last_collection_tm	= 0;


	/**
	 * c/tor.
	 * 
	 * @param name
	 * @param cmdparts
	 * @param attrSep
	 * @param kvSep
	 * @param execPeriod
	 * @param execTimeout
	 * @param storeKey
	 * @param failResponse
	 * @param retries
	 * @throws Exception
	 *             - on creation errors.
	 */
	DefaultProbe(String name, String[] cmdparts, char attrSep, char kvSep, int execPeriod, int execTimeout, String storeKey,
			String failResponse, int retries) throws Exception
	{
		this.name = name;
		this.cmdparts = cmdparts;
		this.attrSep = attrSep;
		this.kvSep = kvSep;
		this.execPeriod = execPeriod;
		this.execTimeout = execTimeout;
		this.storeKey = storeKey;
		this.retries = retries;

		appendItems( failResponse, attrSep, kvSep, error );
	}


	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		log.debug( "Executing" );
		last_event.clear();
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

				last_collection_tm = new Date().getTime();
				appendItems( buf.toString(), attrSep, kvSep, last_event );
				break;
			}
			catch( IOException x )
			{
				log.debug( "failed (I/O error:" + x.getMessage() + ") @ attempt: " + ( tries + 1 ) + "/" + retries );
			}
			catch( Exception x )
			{
				log.warn( "failed (" + x.getMessage() + ") @ attempt: " + ( tries + 1 ) + "/" + retries );
			}

		log.debug( "Done" );
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
	public List<Pair<String, Object>> lastCollected()
	{
		return last_event;
	}


	/**
	 * append the items parsed form the given text to the pool provided.
	 * 
	 * @param text
	 * @param attrSep
	 * @param kvSep
	 * @param pool
	 * @throws Exception
	 *             - on parse errors, but not on <code>null</code> (not checked for).
	 */
	protected void appendItems(String text, char attrSep, char kvSep, List<Pair<String, Object>> pool) throws Exception
	{
		String[] items = text.split( Pattern.quote( Character.toString( attrSep ) ) );

		String kvs = Pattern.quote( Character.toString( kvSep ) );
		for( String attr : items )
		{
			String[] parts = attr.split( kvs );
			if( parts.length != 2 ) throw new Exception( "bad attribute format: " + attr );
			pool.add( new Pair<String, Object>( parts[0], parts[1] ) );
		}
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
