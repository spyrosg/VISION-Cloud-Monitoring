package gr.ntua.vision.monitoring.phony;

import gr.ntua.vision.monitoring.cluster.Configuration;
import gr.ntua.vision.monitoring.ext.local.Catalog;
import gr.ntua.vision.monitoring.ext.local.InMemoryLocalCatalog;
import gr.ntua.vision.monitoring.ext.local.LocalCatalogFactory;
import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.Event.EventType;
import gr.ntua.vision.monitoring.model.Resource;
import gr.ntua.vision.monitoring.model.impl.EventImpl;
import gr.ntua.vision.monitoring.util.Pair;

import java.util.List;

import org.json.JSONException;

import com.google.common.collect.Lists;


/**
 * This is used to insert phony configuration data. It will only write on an instance of {@link InMemoryLocalCatalog}, if not
 * found, it will throw an error.
 */
public abstract class PhonyConfigurationWriter
{
	/** error event **/
	private static final Event	error;

	static
	{
		error = new EventImpl( null, null, "error", EventType.Measurement, Lists.<Resource> newArrayList(), 0, 0, null, null,
				null );
	}


	/**
	 * do the registration of false configuration data.
	 */
	public static void registerPhonyConfig()
	{
		Catalog catalog = LocalCatalogFactory.localCatalogInstance();
		if( !( catalog instanceof InMemoryLocalCatalog ) ) throw new AssertionError();

		final List<Pair<String, Object>> items = Lists.newArrayList();

		items.add( new Pair<String, Object>( Configuration.ScriptsDir, "/home/matron/Work/workspace/vismo/WebContent/scripts" ) );

		final String A = "probe:local";
		items.add( new Pair<String, Object>( Configuration.ProbeNames, new String[] { A } ) );

		String errJson = "";
		try
		{
			errJson = error.toJSON().toString();
		}
		catch( JSONException e )
		{
			e.printStackTrace();
		}

		// probe A:
		items.add( new Pair<String, Object>( A + Configuration.ProbeCommandParts, new String[] { "probe.sh" } ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeExecPeriod, 2 ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeExecTimeout, 2 ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeStoreKey, "vismo.measurements" ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeFail, errJson ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeRetries, 4 ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeInScripts, true ) );

		catalog.put( Configuration.GlobalCfgKey, items );
	}
}
