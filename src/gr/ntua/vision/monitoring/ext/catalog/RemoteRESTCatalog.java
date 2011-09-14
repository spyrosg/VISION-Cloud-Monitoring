package gr.ntua.vision.monitoring.ext.catalog;

import gr.ntua.vision.monitoring.util.Pair;

import java.util.Iterator;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.JSONArray;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * Remote {@link Catalog} interface implementation.
 */
public class RemoteRESTCatalog implements Catalog
{
	/** the jersey client which does all the requests. */
	private final Client	client	= new Client();
	/** the URL to get data from. */
	private final String	url;
	/** the accept string. */
	private final String	accept;


	/**
	 * c/tor.
	 * 
	 * @param url
	 *            delegation URL.
	 * @param accept
	 *            the accept header string.
	 */
	public RemoteRESTCatalog(String url, String accept)
	{
		this.url = url;
		this.accept = accept;
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#put(java.lang.String, java.util.List)
	 */
	@Override
	public void put(String key, List<Pair<String, Object>> items) throws IllegalArgumentException, IllegalStateException
	{
		try
		{
			JSONObject rqst = jsonObject( "keyname", key, "items", jsonArray( items ) );

			WebResource resource = client.resource( url );
			resource.accept( accept ).type( accept );

			resource.put( rqst );
		}
		catch( Throwable x )
		{
			throw new IllegalStateException( x );
		}
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#get(java.lang.String, java.lang.String)
	 */
	@Override
	public Object get(String key, String var) throws IllegalArgumentException, IllegalStateException
	{
		try
		{
			JSONObject rqst = jsonObject( "query_name", "get_value", "query", jsonObject( "keyname", key, "variable", var ) );

			WebResource resource = client.resource( url );
			resource.accept( accept ).type( accept );

			String _json = resource.entity( rqst ).get( ClientResponse.class ).getEntity( String.class );

			JSONObject result = new JSONObject( _json );
			return result.get( "response_data" );
		}
		catch( Throwable x )
		{
			throw new IllegalStateException( x );
		}
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
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#range(java.lang.String, java.lang.String, java.lang.String,
	 *      java.util.List)
	 */
	@Override
	public void range(String key, String min, String max, List<Pair<String, Object>> results) throws IllegalArgumentException,
			IllegalStateException
	{
		try
		{
			JSONObject rqst = jsonObject(	"query_name",
											"query_value_range", //
											"query",
											jsonObject( "keyname", key, //
														"variable_range", jsonObject( "from_variable", min, "to_variable", max ) ) );

			WebResource resource = client.resource( url );
			resource.accept( accept ).type( accept );

			String _json = resource.entity( rqst ).get( ClientResponse.class ).getEntity( String.class );

			JSONObject result = new JSONObject( _json );
			JSONObject collection = result.getJSONObject( "response_data" );
			@SuppressWarnings("unchecked")
			Iterator<String> keys = collection.keys();
			while( keys.hasNext() )
			{
				String k = keys.next();
				results.add( new Pair<String, Object>( k, collection.getString( k ) ) );
			}
		}
		catch( Throwable x )
		{
			throw new IllegalStateException( x );
		}
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#deleteRange(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteRange(String key, String min, String max) throws IllegalArgumentException, IllegalStateException
	{
		try
		{
			JSONObject rqst = jsonObject(	"query_name",
											"delete_by_variable_range", //
											"query",
											jsonObject( "keyname", key, //
														"variable_range", jsonObject( "from_variable", min, "to_variable", max ) ) );

			WebResource resource = client.resource( url );
			resource.accept( accept ).type( accept );

			resource.entity( rqst ).delete();
		}
		catch( Throwable x )
		{
			throw new IllegalStateException( x );
		}
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#get(java.lang.String, java.util.List)
	 */
	@Override
	public void get(String key, List<Pair<String, Object>> items) throws IllegalArgumentException, IllegalStateException
	{
		try
		{
			JSONObject rqst = jsonObject( "query_name", "get_all_items", "query", jsonObject( "keyname", key ) );

			WebResource resource = client.resource( url );
			resource.accept( accept ).type( accept );

			String _json = resource.entity( rqst ).get( ClientResponse.class ).getEntity( String.class );

			JSONObject result = new JSONObject( _json );
			JSONObject collection = result.getJSONObject( "response_data" );
			@SuppressWarnings("unchecked")
			Iterator<String> keys = collection.keys();
			while( keys.hasNext() )
			{
				String k = keys.next();
				items.add( new Pair<String, Object>( k, collection.getString( k ) ) );
			}
		}
		catch( Throwable x )
		{
			throw new IllegalStateException( x );
		}
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#put(java.lang.String, long, java.util.List)
	 */
	@Override
	public void put(String key, long timestamp, List<Pair<String, Object>> items) throws IllegalArgumentException,
			IllegalStateException
	{
		try
		{
			JSONObject rqst = jsonObject( "keyname", key, "timestamp", timestamp, "items", jsonArray( items ) );

			WebResource resource = client.resource( url );
			resource.accept( accept ).type( accept );

			resource.put( rqst );
		}
		catch( Throwable x )
		{
			throw new IllegalStateException( x );
		}
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#timeRange(java.lang.String, long, long, java.util.List)
	 */
	@Override
	public void timeRange(String key, long min, long max, List<Pair<Long, List<Pair<String, Object>>>> results)
			throws IllegalArgumentException, IllegalStateException
	{
		try
		{
			JSONObject rqst = jsonObject(	"query_name",
											"get_timestamp_range", //
											"query",
											jsonObject( "keyname", key, //
														"timestamp_range", jsonObject( "from_timestamp", Long.toString( min ), //
																						"to_timestamp", Long.toString( max ) ) ) );

			WebResource resource = client.resource( url );
			resource.accept( accept ).type( accept );

			String _json = resource.entity( rqst ).get( ClientResponse.class ).getEntity( String.class );

			JSONObject result = new JSONObject( _json );
			JSONObject collection = result.getJSONObject( "response_data" );
			@SuppressWarnings("unchecked")
			Iterator<String> times = collection.keys();
			while( times.hasNext() )
			{
				String t = times.next();
				long tm = Long.parseLong( t );

				JSONObject values = collection.getJSONObject( t );
				List<Pair<String, Object>> items = Lists.newArrayList();

				@SuppressWarnings("unchecked")
				Iterator<String> keys = values.keys();
				while( keys.hasNext() )
				{
					String k = keys.next();
					items.add( new Pair<String, Object>( k, values.getString( k ) ) );
				}

				results.add( new Pair<Long, List<Pair<String, Object>>>( tm, items ) );
			}
		}
		catch( Throwable x )
		{
			throw new IllegalStateException( x );
		}
	}


	/**
	 * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#deleteTimeRange(java.lang.String, long, long)
	 */
	@Override
	public void deleteTimeRange(String key, long min, long max) throws IllegalArgumentException, IllegalStateException
	{
		try
		{
			JSONObject rqst = jsonObject(	"query_name",
											"delete_by_timestamp_range", //
											"query",
											jsonObject( "keyname", key, //
														"timestamp_range", jsonObject( "from_timestamp", Long.toString( min ), //
																						"to_timestamp", Long.toString( max ) ) ) );

			WebResource resource = client.resource( url );
			resource.accept( accept ).type( accept );

			resource.entity( rqst ).delete();
		}
		catch( Throwable x )
		{
			throw new IllegalStateException( x );
		}
	}


	/**
	 * convert the given key value pairs to a json array of objects containing them.
	 * 
	 * @param items
	 *            the items to convert.
	 * @return the JSON array.
	 */
	private JSONArray jsonArray(List<Pair<String, Object>> items)
	{
		List<JSONObject> objects = Lists.newArrayList( Iterables.transform( items,
																			new Function<Pair<String, Object>, JSONObject>() {
																				@Override
																				public JSONObject apply(Pair<String, Object> arg0)
																				{
																					JSONObject obj = new JSONObject();

																					try
																					{
																						obj.put( arg0.a, arg0.b.toString() );
																					}
																					catch( JSONException e )
																					{
																						return null;
																					}

																					return obj;
																				}
																			} ) );
		Iterables.removeIf( objects, Predicates.isNull() );
		return new JSONArray( objects );
	}


	/**
	 * create a json object.
	 * 
	 * @param pairs
	 *            the key-value pairs.
	 * @return the object.
	 */
	private JSONObject jsonObject(Object... pairs)
	{
		if( pairs.length % 2 == 1 ) throw new IllegalArgumentException();
		JSONObject obj = new JSONObject();

		try
		{
			for( int i = 0; i < pairs.length; i += 2 )
				obj.put( pairs[i].toString(), pairs[i + 1] );
		}
		catch( JSONException e )
		{
			return null;
		}

		return obj;
	}
}
