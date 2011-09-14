package gr.ntua.vision.monitoring.ext.catalog;

import gr.ntua.vision.monitoring.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;


/**
 * In memory (test implementation) for the {@link Catalog} interface.
 */
public class InMemoryLocalCatalog implements Catalog
{
	/** plain object store. */
	private final Map<String, SortedMap<String, Object>>			store	= Maps.newHashMap();
	/** timed object store. */
	private final Map<String, SortedMap<Long, Map<String, Object>>>	timed	= Maps.newHashMap();


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#put(java.lang.String, java.util.List)
	 */
	@Override
	public void put(String key, List<Pair<String, Object>> items) throws IllegalArgumentException, IllegalStateException
	{
		SortedMap<String, Object> tmp = store.get( key );

		if( tmp == null )
			store.put( key, Pair.sortedMapOf( items ) );
		else tmp.putAll( Pair.mapOf( items ) );
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#get(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object get(String key, String var) throws IllegalArgumentException, IllegalStateException
	{
		if( !store.containsKey( key ) ) return null;

		return store.get( key ).get( var );
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#as(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T as(String key, String var, Class<T> type) throws IllegalArgumentException, IllegalStateException
	{
		return (T) get( key, var );
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#range(java.lang.String, java.lang.Object, java.lang.Object,
	 *      java.util.List)
	 */
	@Override
	public void range(String key, String min, String max, List<Pair<String, Object>> results) throws IllegalArgumentException,
			IllegalStateException
	{
		if( !store.containsKey( key ) ) return;

		results.addAll( Pair.pairsOf( store.get( key ).subMap( min, max ) ) );
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#deleteRange(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void deleteRange(String key, String min, String max) throws IllegalArgumentException, IllegalStateException
	{
		if( !store.containsKey( key ) ) return;

		SortedMap<String, Object> del = store.get( key ).subMap( min, max );
		Iterables.removeAll( del.entrySet(), del.entrySet() );
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#get(java.lang.String, java.util.List)
	 */
	@Override
	public void get(String key, List<Pair<String, Object>> items) throws IllegalArgumentException, IllegalStateException
	{
		if( !store.containsKey( key ) ) return;

		items.addAll( Pair.pairsOf( store.get( key ) ) );
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#put(java.lang.String, long, java.util.List)
	 */
	@Override
	public void put(String key, long timestamp, List<Pair<String, Object>> items) throws IllegalArgumentException,
			IllegalStateException
	{
		SortedMap<Long, Map<String, Object>> tmp = timed.get( key );

		if( tmp == null )
		{
			tmp = Maps.newTreeMap();
			tmp.put( timestamp, Pair.mapOf( items ) );
			timed.put( key, tmp );
		}
		else
		{
			Map<String, Object> inner = tmp.get( timestamp );
			if( inner != null )
				inner.putAll( Pair.mapOf( items ) );
			else tmp.put( timestamp, Pair.mapOf( items ) );
		}
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#timeRange(java.lang.String, long, long, java.util.List)
	 */
	@Override
	public void timeRange(String key, long min, long max, List<Pair<Long, List<Pair<String, Object>>>> results)
			throws IllegalArgumentException, IllegalStateException
	{
		SortedMap<Long, Map<String, Object>> tmp = timed.get( key );
		if( tmp == null ) return;

		for( Map.Entry<Long, Map<String, Object>> m : tmp.subMap( min, max ).entrySet() )
			results.add( new Pair<Long, List<Pair<String, Object>>>( m.getKey(), Pair.pairsOf( m.getValue() ) ) );
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#deleteTimeRange(java.lang.String, long, long)
	 */
	@Override
	public void deleteTimeRange(String key, long min, long max) throws IllegalArgumentException, IllegalStateException
	{
		SortedMap<Long, Map<String, Object>> del = timed.get( key );
		if( del == null ) return;

		Iterables.removeAll( del.entrySet(), del.entrySet() );
	}
}
