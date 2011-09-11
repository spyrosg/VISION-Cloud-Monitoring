package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.ext.local.Catalog;
import gr.ntua.vision.monitoring.ext.local.LocalCatalogFactory;
import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.impl.EventImpl;
import gr.ntua.vision.monitoring.util.Pair;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * This is used to periodically check for events in a cluster DB.
 */
public class EventReader extends Thread
{
	/** data required to access a cluster. */
	private class ClusterData
	{
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
			this.catalog = LocalCatalogFactory.localCatalogInstance( cluster );
			this.key = key;
			this.lastInspection = 0;
		}
	}

	/** the rule engine. */
	private final RuleEngine				ruleEngine;
	/** the clusters to fetch events from. */
	private final Map<String, ClusterData>	clusters	= new ConcurrentSkipListMap<String, ClusterData>();


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
		clusters.put( cluster, new ClusterData( cluster, key ) );
	}


	/**
	 * remove a cluster from this class.
	 * 
	 * @param cluster
	 *            the cluster to remove.
	 */
	public void remove(String cluster)
	{
		clusters.remove( cluster );
	}


	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		while( true )
			try
			{
				Thread.sleep( 500 );

				List<Pair<String, Object>> pairs = Lists.newArrayList();

				long now = new Date().getTime();
				for( ClusterData ctlg : clusters.values() )
				{
					pairs.clear();
					ctlg.catalog.timeRange( ctlg.key, ctlg.lastInspection, now, pairs );

					ruleEngine.push( Iterables.transform( pairs, new Function<Pair<String, Object>, Event>() {
						@Override
						public Event apply(Pair<String, Object> arg0)
						{
							try
							{
								return new EventImpl( new JSONObject( arg0.b.toString() ) );
							}
							catch( JSONException x )
							{
								x.printStackTrace();
								return null;
							}
						}
					} ) );
				}
			}
			catch( InterruptedException x )
			{
				// ignore.
				return;
			}
	}
}
