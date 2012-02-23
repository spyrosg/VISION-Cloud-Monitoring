package gr.ntua.vision.monitoring.ext.iface;

import gr.ntua.vision.monitoring.cluster.ClusterMonitoring;
import gr.ntua.vision.monitoring.cluster.ProbeExecutor;
import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.impl.EventImpl;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONStringer;
import org.codehaus.jettison.json.JSONWriter;


/**
 * Monitoring push example interface.
 */
@Path("/push/event")
public class MonitoringPushInterface extends XdasPublisher {
    /**
     * This is the service implementing method.
     * 
     * @param eventJSON
     *            the JSON string which specifies the event.
     * @return the operation status.
     * @throws Exception
     * @throws MalformedURLException
     */
    @POST
    @Produces("application/json")
    public String receiveEvent(@FormParam("event") final String eventJSON) throws MalformedURLException, Exception {
        final JSONObject jsonEvent = new JSONObject( eventJSON );

        handleEvent( new EventImpl( jsonEvent ) );

        final JSONWriter wr = new JSONStringer().object();
        wr.key( "status" ).value( "ok" );
        return wr.endObject().toString();
    }


    /**
     * get the time zone string.
     * 
     * @return the string.
     */
    @SuppressWarnings("unused")
    private String getStrTimeZone() {
        final TimeZone timeZone = TimeZone.getDefault();

        final int iOffsetSeconds = -( timeZone.getRawOffset() / 1000 );

        final int iOffsetHours = iOffsetSeconds / 3600;
        final int iOffsetMinutes = iOffsetSeconds % 3600 / 60;
        final StringBuffer strTimeZone = new StringBuffer();
        strTimeZone.append( timeZone.getDisplayName( false, 0 ) );
        strTimeZone.append( iOffsetHours );
        if( iOffsetMinutes != 0 ) {
            strTimeZone.append( "%:" );
            strTimeZone.append( iOffsetMinutes );
        }

        if( timeZone.useDaylightTime() )
            strTimeZone.append( timeZone.getDisplayName( true, 0 ) );
        return strTimeZone.toString();
    }


    /**
     * get the time offset string.
     * 
     * @return the string.
     */
    private long getTimeOffSet() {
        final Date date = new Date();
        final TimeZone cetTime = TimeZone.getTimeZone( "CET" );
        final DateFormat cetFormat = new SimpleDateFormat();
        cetFormat.setTimeZone( cetTime );
        return date.getTime() / 1000L;
    }


    /**
     * actual event handler.
     * 
     * @param event
     *            event to handle.
     */
    private void handleEvent(final Event event) {
        if( ClusterMonitoring.instance.isInstanceAlive() ) //
            ProbeExecutor.saveEvents( new Date().getTime(), Arrays.asList( event ), null );
    }
}
