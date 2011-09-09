package gr.ntua.vision.monitoring.phony;

import gr.ntua.vision.monitoring.core.Configuration;
import gr.ntua.vision.monitoring.ext.local.InMemoryLocalCatalog;
import gr.ntua.vision.monitoring.ext.local.LocalCatalog;
import gr.ntua.vision.monitoring.ext.local.LocalCatalogFactory;
import gr.ntua.vision.monitoring.util.Pair;

import java.util.List;

import com.google.common.collect.Lists;


/**
 * This is used to insert phony configuration data. It will only write on an instance of {@link InMemoryLocalCatalog}, if not
 * found, it will throw an error.
 */
public abstract class PhonyConfigurationWriter
{
	/**
	 * do the registration of false configuration data.
	 */
	public static void registerPhonyConfig()
	{
		LocalCatalog catalog = LocalCatalogFactory.localCatalogInstance();
		if( !( catalog instanceof InMemoryLocalCatalog ) ) throw new AssertionError();

		final List<Pair<String, Object>> items = Lists.newArrayList();

		items.add( new Pair<String, Object>( Configuration.ScriptsDir, "/home/matron/Work/workspace/vismo/WebContent/scripts" ) );

		final String A = "probeA:mem:local";
		final String B = "probeB:load:local";
		final String C = "probeC:load:remote";
		items.add( new Pair<String, Object>( Configuration.ProbeNames, new String[] { A, B, C } ) );

		// probe A:
		items.add( new Pair<String, Object>( A + Configuration.ProbeCommandParts, new String[] { "mem.sh" } ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeAttributeListSeparator, '\n' ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeKeyValueSeparator, '=' ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeExecPeriod, 2 ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeExecTimeout, 2 ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeStoreKey, "vismo.memory" ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeFail, "host=localhost\nfailed=true" ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeRetries, 4 ) );
		items.add( new Pair<String, Object>( A + Configuration.ProbeInScripts, true ) );

		// probe B:
		items.add( new Pair<String, Object>( B + Configuration.ProbeCommandParts, new String[] { "load.sh" } ) );
		items.add( new Pair<String, Object>( B + Configuration.ProbeAttributeListSeparator, '\n' ) );
		items.add( new Pair<String, Object>( B + Configuration.ProbeKeyValueSeparator, '=' ) );
		items.add( new Pair<String, Object>( B + Configuration.ProbeExecPeriod, 2 ) );
		items.add( new Pair<String, Object>( B + Configuration.ProbeExecTimeout, 2 ) );
		items.add( new Pair<String, Object>( B + Configuration.ProbeStoreKey, "vismo.load" ) );
		items.add( new Pair<String, Object>( B + Configuration.ProbeFail, "host=localhost\nfailed=true" ) );
		items.add( new Pair<String, Object>( B + Configuration.ProbeRetries, 4 ) );
		items.add( new Pair<String, Object>( B + Configuration.ProbeInScripts, true ) );

		// probe C:
		items.add( new Pair<String, Object>( C + Configuration.ProbeCommandParts, new String[] { "/usr/bin/ssh", "orthanc@147.102.19.45", "/home/orthanc/bin/load.sh" } ) );
		items.add( new Pair<String, Object>( C + Configuration.ProbeAttributeListSeparator, '\n' ) );
		items.add( new Pair<String, Object>( C + Configuration.ProbeKeyValueSeparator, '=' ) );
		items.add( new Pair<String, Object>( C + Configuration.ProbeExecPeriod, 2 ) );
		items.add( new Pair<String, Object>( C + Configuration.ProbeExecTimeout, 2 ) );
		items.add( new Pair<String, Object>( C + Configuration.ProbeStoreKey, "vismo.load" ) );
		items.add( new Pair<String, Object>( C + Configuration.ProbeFail, "host=147.102.19.45\nfailed=true" ) );
		items.add( new Pair<String, Object>( C + Configuration.ProbeRetries, 4 ) );
		items.add( new Pair<String, Object>( C + Configuration.ProbeInScripts, false ) );

		catalog.put( Configuration.GlobalCfgKey, items );
	}
}
