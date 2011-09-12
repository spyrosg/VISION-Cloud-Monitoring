package gr.ntua.vision.monitoring.ext.iface;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONStringer;


/**
 * This contains the cluster REST interface entries.
 */
@Path("/cluster")
public class ClusterInterface
{
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
		log.debug( "REST: setClusterMonitoringParameter('" + name + "')" );
		return new JSONStringer().object().key( "status" ).value( "ok" ).endObject().toString();
	}
}
