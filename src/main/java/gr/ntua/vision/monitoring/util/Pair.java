package gr.ntua.vision.monitoring.util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * a pair of items.
 * 
 * @param <T1>
 *            first item type.
 * @param <T2>
 *            second item type.
 */
public class Pair<T1, T2>
{
	/** the first item. */
	public final T1			a;
	/** the second item. */
	public final T2			b;
	/**
	 * when <code>true</code> the {@link #hashCode()} and {@link #equals(Object)} implementations consider the pairs (a,b) and
	 * (b,a) the same ones.
	 */
	public final boolean	interchangeable;


	/**
	 * c/tor.
	 * 
	 * @param a
	 * @param b
	 */
	public Pair(final T1 a, final T2 b)
	{
		this( a, b, false );
	}


	/**
	 * c/tor.
	 * 
	 * @param a
	 * @param b
	 * @param interchangeable
	 *            when <code>true</code> the {@link #hashCode()} and {@link #equals(Object)} implementations consider the pairs
	 *            (a,b) and (b,a) the same ones.
	 */
	public Pair(final T1 a, final T2 b, final boolean interchangeable)
	{
		this.a = a;
		this.b = b;
		this.interchangeable = interchangeable;

		if( interchangeable && a.getClass() != b.getClass() ) throw new IllegalArgumentException();
	}


	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( a == null ) ? 0 : a.hashCode() ) + ( ( b == null ) ? 0 : b.hashCode() );
		return result;
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if( this == obj ) return true;
		if( obj == null ) return false;
		if( getClass() != obj.getClass() ) return false;
		@SuppressWarnings("rawtypes")
		final Pair other = (Pair) obj;
		boolean checkInverse = false;
		do
		{
			if( a == null )
			{
				if( other.a != null )
				{
					checkInverse = true;
					break;
				}
			}
			else if( !a.equals( other.a ) )
			{
				checkInverse = true;
				break;
			}
			if( b == null )
			{
				if( other.b != null )
				{
					checkInverse = true;
					break;
				}
			}
			else if( !b.equals( other.b ) )
			{
				checkInverse = true;
				break;
			}
		}
		while( false );
		if( checkInverse )
		{
			if( !interchangeable ) return false;

			if( a == null )
			{
				if( other.b != null ) return false;
			}
			else if( !a.equals( other.b ) ) return false;
			if( b == null )
			{
				if( other.a != null ) return false;
			}
			else if( !b.equals( other.a ) ) return false;
		}
		return true;
	}


	/**
	 * get a comparator against the first pair element.
	 * 
	 * @param <T1>
	 *            first item type (must be comparable).
	 * @param <T2>
	 *            second item type.
	 * @param inverse
	 *            when <code>true</code> an inverse ordering comparator order will be produced.
	 * @return the comparator.
	 */
	public static <T1 extends Comparable<T1>, T2> Comparator<Pair<T1, T2>> compare_A(final boolean inverse)
	{
		return new Comparator<Pair<T1, T2>>() {
			@Override
			public int compare(final Pair<T1, T2> o1, final Pair<T1, T2> o2)
			{
				return ( inverse ? -1 : 1 ) * o1.a.compareTo( o2.a );
			}
		};
	}


	/**
	 * get a comparator against the second pair element.
	 * 
	 * @param <T1>
	 *            first item type.
	 * @param <T2>
	 *            second item type (must be comparable).
	 * @param inverse
	 *            when <code>true</code> an inverse ordering comparator order will be produced.
	 * @return the comparator.
	 */
	public static <T1, T2 extends Comparable<T2>> Comparator<Pair<T1, T2>> compare_B(final boolean inverse)
	{
		return new Comparator<Pair<T1, T2>>() {
			@Override
			public int compare(final Pair<T1, T2> o1, final Pair<T1, T2> o2)
			{
				return ( inverse ? -1 : 1 ) * o1.b.compareTo( o2.b );
			}
		};
	}


	/**
	 * list the associations in the given map.
	 * 
	 * @param <T1>
	 *            first item type.
	 * @param <T2>
	 *            second item type.
	 * @param map
	 *            the associations to list.
	 * @return the list of associations.
	 */
	public static <T1, T2> List<Pair<T1, T2>> pairsOf(final Map<T1, T2> map)
	{
		final List<Pair<T1, T2>> list = Lists.newArrayList();

		Iterators.addAll( list, Iterators.transform( map.entrySet().iterator(), new Function<Map.Entry<T1, T2>, Pair<T1, T2>>() {
			@Override
			public Pair<T1, T2> apply(final Entry<T1, T2> input)
			{
				return new Pair<T1, T2>( input.getKey(), input.getValue() );
			}
		} ) );

		return list;
	}


	/**
	 * get a map with the associations defined in the given list.
	 * 
	 * @param pairs
	 *            the pairs.
	 * @return the map instance.
	 */
	public static <T1, T2> Map<T1, T2> mapOf(final List<Pair<T1, T2>> pairs)
	{
		Map<T1, T2> map = Maps.newHashMap();

		for( Pair<T1, T2> pair : pairs )
			map.put( pair.a, pair.b );

		return map;
	}


	/**
	 * get a map with the associations defined in the given list.
	 * 
	 * @param pairs
	 *            the pairs.
	 * @return the map instance.
	 */
	public static <T1 extends Comparable< T1 >, T2> SortedMap<T1, T2> sortedMapOf(final List<Pair<T1, T2>> pairs)
	{
		SortedMap<T1, T2> map = Maps.newTreeMap();

		for( Pair<T1, T2> pair : pairs )
			map.put( pair.a, pair.b );

		return map;
	}
}
