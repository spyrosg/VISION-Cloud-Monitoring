package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.ext.catalog.Catalog;
import gr.ntua.vision.monitoring.ext.catalog.LocalCatalogFactory;
import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.impl.EventImpl;
import gr.ntua.vision.monitoring.util.Pair;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * This is used to periodically check for events in a cluster DB.
 */
public class EventReader extends Thread
{
	/** data required to access a cluster. */
	private class ClusterData implements Comparable<ClusterData>
	{
		/** cluster url */
		final String	cluster;
		/** the cluster's catalog */
		final Catalog	catalog;
		/** the key under which events are stored. */
		final String	key;
		/** the tmestamp of last inspection */
		long			lastInspection;


		/**
		 * c/tor.
		 * 
		 * @param cluster
		 * @param key
		 */
		ClusterData(String cluster, String key)
		{
			this.cluster = cluster;
			this.catalog = LocalCatalogFactory.localCatalogInstance( cluster );
			this.key = key;
			this.lastInspection = 0;
		}


		@Override
		public int compareTo(ClusterData arg0)
		{
			if( !key.equals( arg0.key ) ) //
				return key.toString().compareTo( arg0.key );

			return hashCode() - arg0.hashCode();
		}
	}

	/** the logger. */
	@SuppressWarnings("all")
	private static final Logger		log			= Logger.getLogger( EventReader.class );
	/** the rule engine. */
	private final RuleEngine		ruleEngine;
	/** the clusters to fetch events from. */
	private final Set<ClusterData>	clusters	= new ConcurrentSkipListSet<ClusterData>();


	/**
	 * c/tor.
	 * 
	 * @param ruleEngine
	 */
	public EventReader(RuleEngine ruleEngine)
	{
		this.ruleEngine = ruleEngine;
	}


	/**
	 * register a cluster with this class.
	 * 
	 * @param cluster
	 *            the cluster to register.
	 * @param key
	 *            key to obtain.
	 */
	public void register(String cluster, String key)
	{
		clusters.add( new ClusterData( cluster, key ) );
	}


	/**
	 * clear the clusters pool.
	 */
	public void clear()
	{
		clusters.clear();
	}


	/**
	 * remove a cluster from this class.
	 * 
	 * @param cluster
	 *            the cluster to remove.
	 */
	public void remove(final String cluster)
	{
		Iterables.removeIf( clusters, new Predicate<ClusterData>() {
			@Override
			public boolean apply(ClusterData arg0)
			{
				return arg0.cluster.equals( cluster );
			}
		} );
	}


	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		log.info( "Event reader starts" );
		try
		{
			while( true )
				try
				{
					Thread.sleep( 5000 );

					List<Pair<Long, List<Pair<String, Object>>>> pairs = Lists.newArrayList();

					long now = new Date().getTime();
					for( ClusterData ctlg : clusters )
					{
						pairs.clear();
						ctlg.catalog.timeRange( ctlg.key, ctlg.lastInspection, now, pairs );
						// ctlg.catalog.deleteTimeRange( ctlg.key, ctlg.lastInspection, now );
						ctlg.lastInspection = now;

						if( pairs.size() > 0 )
						{
							log.debug( ctlg.key + ": pushing " + pairs.size() + " events." );

							// for( Pair<Long, List<Pair<String, Object>>> pair : pairs )

							ruleEngine.push( Iterables.concat( Iterables
									.transform( pairs, new Function<Pair<Long, List<Pair<String, Object>>>, Iterable<Event>>() {
										@Override
										public Iterable<Event> apply(Pair<Long, List<Pair<String, Object>>> arg0)
										{
											return Iterables.filter( Iterables
													.transform( arg0.b, new Function<Pair<String, Object>, Event>() {
														@Override
														public Event apply(Pair<String, Object> arg0)
														{
															try
															{
																return new EventImpl( new JSONObject( arg0.b.toString() ) );
															}
															catch( JSONException x )
															{
																// ignore.
																return null;
															}
														}
													} ), Predicates.not( Predicates.isNull() ) );
										}
									} ) ) );
						}
					}
				}
				catch( InterruptedException x )
				{
					return;
				}
				catch( Throwable x )
				{
					// ignore.
					x.printStackTrace();
				}
		}
		finally
		{
			log.info( "Event reader stops" );
		}
	}
}
