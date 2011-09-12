package gr.ntua.vision.monitoring.ext.iface;

import gr.ntua.vision.monitoring.cluster.ClusterMonitoring;
import gr.ntua.vision.monitoring.cluster.ProbeExecutor;
import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.impl.EventImpl;

import java.util.Arrays;
import java.util.Date;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONWriter;


/**
 * Monitoring push example interface.
 */
public class MonitoringPushInterface
{
	/**
	 * This is the service implementing method.
	 * 
	 * @param eventJSON
	 *            the JSON string which specifies the event.
	 * @return the operation status.
	 * @throws JSONException
	 */
	@POST
	@Path("/push/event")
	@Produces("application/json")
	public String receiveEvent(@QueryParam("event") String eventJSON) throws JSONException
	{
		handleEvent( new EventImpl( new JSONObject( eventJSON ) ) );

		final JSONWriter wr = new JSONStringer().object();
		wr.key( "status" ).value( "ok" );
		return wr.endObject().toString();
	}


	/**
	 * actual event handler.
	 * 
	 * @param event
	 *            event to handle.
	 */
	public void handleEvent(Event event)
	{
		if( ClusterMonitoring.instance.isInstanceAlive() ) //
			ProbeExecutor.saveEvents( new Date().getTime(), Arrays.asList( event ), null );
	}
}
