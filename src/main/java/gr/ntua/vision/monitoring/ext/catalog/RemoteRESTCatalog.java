package gr.ntua.vision.monitoring.ext.catalog;

import gr.ntua.vision.monitoring.util.Pair;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.common.collect.Lists;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;


/**
 * Remote {@link Catalog} interface implementation.
 */
public class RemoteRESTCatalog implements Catalog {
    /** debug option of this. */
    public static boolean       debugIface = true;
    /** the logger. */
    @SuppressWarnings("all")
    private static final Logger log        = Logger.getLogger( RemoteRESTCatalog.class );
    /** the accept string. */
    private final String        accept;
    /** the jersey client which does all the requests. */
    private final Client        client     = new Client();
    /** the URL to get data from. */
    private final String        url;


    /**
     * c/tor.
     * 
     * @param url
     *            delegation URL.
     * @param accept
     *            the accept header string.
     */
    public RemoteRESTCatalog(final String url, final String accept) {
        this.url = url;
        this.accept = accept;
    }


    /**
     * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#as(java.lang.String, java.lang.String, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T as(final String key, final String var, final Class<T> type) throws IllegalArgumentException,
            IllegalStateException {
        return (T) get( key, var );
    }


    /**
     * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#deleteRange(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void deleteRange(final String key, final String min, final String max) throws IllegalArgumentException,
            IllegalStateException {
        try {
            final JSONObject rqst = jsonObject( "query_name",
                                                "delete_by_variable_range", //
                                                "query",
                                                jsonObject( "keyname",
                                                            key, //
                                                            "variable_range",
                                                            jsonObject( "from_variable", min, "to_variable", max ) ) );

            final WebResource resource = client.resource( url );
            final Builder builder = resource.accept( accept ).header( "Content-Type", accept );

            final ClientResponse response = builder.entity( rqst.toString() ).delete( ClientResponse.class );
            log.debug( "DELETE " + resource.toString() + " :: " + response.getStatus() + "\n<<<<\n" + rqst.toString() );
        } catch( final Throwable x ) {
            throw new IllegalStateException( x );
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#deleteTimeRange(java.lang.String, long, long)
     */
    @Override
    public void deleteTimeRange(final String key, final long min, final long max) throws IllegalArgumentException,
            IllegalStateException {
        try {
            final JSONObject rqst = jsonObject( "query_name",
                                                "delete_by_timestamp_range", //
                                                "query",
                                                jsonObject( "keyname",
                                                            key, //
                                                            "timestamp_range",
                                                            jsonObject( "from_timestamp", Long.toString( min ), //
                                                                        "to_timestamp", Long.toString( max ) ) ) );

            final WebResource resource = client.resource( url );
            final Builder builder = resource.accept( accept ).header( "Content-Type", accept );

            final ClientResponse response = builder.entity( rqst.toString() ).delete( ClientResponse.class );
            log.debug( "DELETE " + resource.toString() + " :: " + response.getStatus() + "\n<<<<\n" + rqst.toString() );
        } catch( final Throwable x ) {
            throw new IllegalStateException( x );
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#get(java.lang.String, java.util.List)
     */
    @Override
    public void get(final String key, final List<Pair<String, Object>> items) throws IllegalArgumentException,
            IllegalStateException {
        try {
            final JSONObject rqst = jsonObject( "query_name", "get_all_items", "query", jsonObject( "keyname", key ) );

            final WebResource resource = client.resource( url );
            final Builder builder = resource.accept( accept ).header( "Content-Type", accept );

            final ClientResponse response = builder.entity( rqst.toString() ).post( ClientResponse.class );
            final String _json = response.getStatus() == 200 ? response.getEntity( String.class ) : "";

            log.debug( "POST " + resource.toString() + " :: " + response.getStatus() + "\n<<<<\n" + rqst.toString() + "\n>>>>\n"
                    + "Response: " + _json.length() + " chars." );

            if( response.getStatus() != 200 )
                return;

            final JSONObject result = new JSONObject( _json );
            final JSONObject collection = result.getJSONObject( "response_data" );
            @SuppressWarnings("unchecked")
            final Iterator<String> keys = collection.keys();
            while( keys.hasNext() ) {
                final String k = keys.next();
                items.add( new Pair<String, Object>( k, collection.getString( k ) ) );
            }
        } catch( final Throwable x ) {
            throw new IllegalStateException( x );
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#get(java.lang.String, java.lang.String)
     */
    @Override
    public Object get(final String key, final String var) throws IllegalArgumentException, IllegalStateException {
        try {
            final JSONObject rqst = jsonObject( "query_name", "get_value", "query", jsonObject( "keyname", key, "variable", var ) );

            final WebResource resource = client.resource( url );
            final Builder builder = resource.accept( accept ).header( "Content-Type", accept );

            final ClientResponse response = builder.entity( rqst.toString() ).post( ClientResponse.class );
            final String _json = response.getStatus() == 200 ? response.getEntity( String.class ) : "";

            log.debug( "POST " + resource.toString() + " :: " + response.getStatus() + "\n<<<<\n" + rqst.toString() + "\n>>>>\n"
                    + "Response: " + _json.length() + " chars." );

            if( response.getStatus() != 200 )
                return null;

            final JSONObject result = new JSONObject( _json );
            return result.get( "response_data" );
        } catch( final Throwable x ) {
            throw new IllegalStateException( x );
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#put(java.lang.String, java.util.List)
     */
    @Override
    public void put(final String key, final List<Pair<String, Object>> items) throws IllegalArgumentException,
            IllegalStateException {
        try {
            final JSONObject rqst = jsonObject( "keyname", key, "items", list2JsonObject( items ) );

            final WebResource resource = client.resource( url );
            final Builder builder = resource.accept( accept ).header( "Content-Type", accept );

            final ClientResponse response = builder.put( ClientResponse.class, rqst.toString() );
            log.debug( "PUT " + resource.toString() + " :: " + response.getStatus() + "\n<<<<\n" + rqst.toString() );
        } catch( final Throwable x ) {
            throw new IllegalStateException( x );
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#put(java.lang.String, long, java.util.List)
     */
    @Override
    public void put(final String key, final long timestamp, final List<Pair<String, Object>> items)
            throws IllegalArgumentException, IllegalStateException {
        try {
            final JSONObject rqst = jsonObject( "keyname", key, "timestamp", Long.toString( timestamp ), "items",
                                                list2JsonObject( items ) );

            final WebResource resource = client.resource( url );
            final Builder builder = resource.accept( accept ).header( "Content-Type", accept );

            final ClientResponse response = builder.put( ClientResponse.class, rqst.toString() );
            log.debug( "PUT " + resource.toString() + " :: " + response.getStatus() + "\n<<<<\n" + rqst.toString() );
        } catch( final Throwable x ) {
            throw new IllegalStateException( x );
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#range(java.lang.String, java.lang.String, java.lang.String,
     *      java.util.List)
     */
    @Override
    public void range(final String key, final String min, final String max, final List<Pair<String, Object>> results)
            throws IllegalArgumentException, IllegalStateException {
        try {
            final JSONObject rqst = jsonObject( "query_name",
                                                "get_value_range", //
                                                "query",
                                                jsonObject( "keyname",
                                                            key, //
                                                            "variable_range",
                                                            jsonObject( "from_variable", min, "to_variable", max ) ) );

            final WebResource resource = client.resource( url );
            final Builder builder = resource.accept( accept ).header( "Content-Type", accept );

            final ClientResponse response = builder.entity( rqst.toString() ).post( ClientResponse.class );
            final String _json = response.getStatus() == 200 ? response.getEntity( String.class ) : "";

            log.debug( "POST " + resource.toString() + " :: " + response.getStatus() + "\n<<<<\n" + rqst.toString() + "\n>>>>\n"
                    + "Response: " + _json.length() + " chars." );

            if( response.getStatus() != 200 )
                return;

            final JSONObject result = new JSONObject( _json );
            final JSONObject collection = result.getJSONObject( "response_data" );
            @SuppressWarnings("unchecked")
            final Iterator<String> keys = collection.keys();
            while( keys.hasNext() ) {
                final String k = keys.next();
                results.add( new Pair<String, Object>( k, collection.getString( k ) ) );
            }
        } catch( final Throwable x ) {
            throw new IllegalStateException( x );
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.ext.catalog.Catalog#timeRange(java.lang.String, long, long, java.util.List)
     */
    @Override
    public void timeRange(final String key, final long min, final long max,
            final List<Pair<Long, List<Pair<String, Object>>>> results) throws IllegalArgumentException, IllegalStateException {
        try {
            final JSONObject rqst = jsonObject( "query_name",
                                                "get_timestamp_range", //
                                                "query",
                                                jsonObject( "keyname",
                                                            key, //
                                                            "timestamp_range",
                                                            jsonObject( "from_timestamp", Long.toString( min ), //
                                                                        "to_timestamp", Long.toString( max ) ) ) );

            final WebResource resource = client.resource( url );
            final Builder builder = resource.accept( accept ).header( "Content-Type", accept );

            final ClientResponse response = builder.entity( rqst.toString() ).post( ClientResponse.class );
            final String _json = response.getStatus() == 200 ? response.getEntity( String.class ) : "";

            log.debug( "POST " + resource.toString() + " :: " + response.getStatus() + "\n<<<<\n" + rqst.toString() + "\n>>>>\n"
                    + "Response: " + _json.length() + " chars." );

            if( response.getStatus() != 200 )
                return;

            final JSONObject result = new JSONObject( _json );
            @SuppressWarnings("unchecked")
            final Iterator<String> times = result.keys();
            while( times.hasNext() ) {
                final String t = times.next();
                final long tm = Long.parseLong( t );

                final JSONObject values = result.getJSONObject( t );
                final List<Pair<String, Object>> items = Lists.newArrayList();

                @SuppressWarnings("unchecked")
                final Iterator<String> keys = values.keys();
                while( keys.hasNext() ) {
                    final String k = keys.next();
                    items.add( new Pair<String, Object>( k, values.getString( k ) ) );
                }

                results.add( new Pair<Long, List<Pair<String, Object>>>( tm, items ) );
            }
        } catch( final Throwable x ) {
            throw new IllegalStateException( x );
        }
    }


    /**
     * create a json object.
     * 
     * @param pairs
     *            the key-value pairs.
     * @return the object.
     */
    private JSONObject jsonObject(final Object... pairs) {
        if( pairs.length % 2 == 1 )
            throw new IllegalArgumentException();
        final JSONObject obj = new JSONObject();

        try {
            for( int i = 0; i < pairs.length; i += 2 )
                obj.put( pairs[i].toString(), pairs[i + 1] );
        } catch( final JSONException e ) {
            return null;
        }

        return obj;
    }


    /**
     * convert the given key value pairs to a json array of objects containing them.
     * 
     * @param items
     *            the items to convert.
     * @return the JSON array.
     * @throws JSONException
     */
    private JSONObject list2JsonObject(final List<Pair<String, Object>> items) throws JSONException {
        final JSONObject objects = new JSONObject();

        for( final Pair<String, Object> pair : items )
            objects.put( pair.a, pair.b.toString() );

        return objects;
    }
}
