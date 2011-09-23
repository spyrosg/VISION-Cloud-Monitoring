package gr.ntua.vision.monitoring.ext.iface;

import gr.ntua.vision.monitoring.VismoCtxListener;
import gr.ntua.vision.monitoring.cluster.ClusterMonitoring;
import gr.ntua.vision.monitoring.ext.catalog.LocalCatalogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONStringer;

import com.google.common.base.Function;


/**
 * This contains the cluster REST interface entries.
 */
@Path("/cluster")
public class ClusterInterface
{
	/** variables that may be handled. */
	private enum Variables
	{
		/***/
		Alive(new Function<String, Void>() {
			@Override
			public Void apply(String arg0)
			{
				if( Boolean.parseBoolean( arg0 ) )
				{
					if( !VismoCtxListener.instance().isAlive( ClusterMonitoring.class ) ) //
						try
						{
							VismoCtxListener.instance().launch( ClusterMonitoring.class );
						}
						catch( Throwable x )
						{
							throw new RuntimeException( x );
						}
				}
				else
				{
					if( VismoCtxListener.instance().isAlive( ClusterMonitoring.class ) )
						VismoCtxListener.instance().shutdown( ClusterMonitoring.class );
				}
				return null;
			}
		}),
		/***/
		LocalCatalog(new Function<String, Void>() {
			@Override
			public Void apply(String arg0)
			{
				LocalCatalogFactory.setLocalURL( arg0 );
				return null;
			}
		}),
		/***/
		;

		/** value handler */
		final Function<String, Void>	handler;


		/**
		 * c/tor.
		 * 
		 * @param handler
		 */
		private Variables(Function<String, Void> handler)
		{
			this.handler = handler;
		}
	}

	/** the logger. */
	@SuppressWarnings("all")
	private static final Logger	log	= Logger.getLogger( ClusterInterface.class );


	/**
	 * This operation is used to change the parameter of the cluster wide monitoring component.
	 * 
	 * @param name
	 *            the name of the parameter to change.
	 * @param value
	 *            the value to set to the parameter.
	 * @return the operation's success status.
	 * @throws JSONException
	 */
	@GET
	@Path("/setClusterMonitoringParameter")
	@Produces("application/json")
	public String setClusterMonitoringParameter(@QueryParam("name") String name, @QueryParam("value") String value)
			throws JSONException
	{
		log.debug( "REST: setClusterMonitoringParameter('" + name + "' -> '" + value + "')" );

		if( !name.equals( Variables.Alive.toString() ) && !ClusterMonitoring.instance.isInstanceAlive() )
		{
			log.warn( "Cluster instance down, ignoring request" );
			return new JSONStringer().object().key( "status" ).value( "service down" ).endObject().toString();
		}

		Variables var = Variables.valueOf( name );
		if( var != null ) var.handler.apply( value );

		return new JSONStringer().object().key( "status" ).value( "ok" ).endObject().toString();
	}
}
