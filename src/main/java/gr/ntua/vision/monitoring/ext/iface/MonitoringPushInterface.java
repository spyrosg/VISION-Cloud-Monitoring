package gr.ntua.vision.monitoring.ext.iface;

import gr.ntua.vision.monitoring.cluster.ClusterMonitoring;
import gr.ntua.vision.monitoring.cluster.ProbeExecutor;
import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.impl.EventImpl;
import it.eng.compliance.persistenceservice.model.XdasV1Model;
import it.eng.compliance.xdas.parser.XDasEventType;
import it.eng.compliance.xdas.parser.XdasOutcomes;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
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

        if( jsonEvent.has( "xdasType" ) )
            pushXdas( new URL( jsonEvent.getString( "url_source" ) ), new URL( jsonEvent.optString( "url_target" ) ),
                      jsonEvent.isNull( "user" ) ? null : jsonEvent.optString( "user" ),
                      jsonEvent.isNull( "tenant" ) ? null : jsonEvent.optString( "tenant" ), jsonEvent.getInt( "xdasType" ),
                      jsonEvent.getInt( "xdasStatus" ), jsonEvent.getString( "params_str" ) );

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


    /**
     * generate and push the XDAS event that is emitted by the given data.
     * 
     * @param source
     *            action source
     * @param target
     *            action target. It may be <code>null</code>.
     * @param user
     *            user performing the action
     * @param tenant
     *            tenant performing the action
     * @param xdasType
     *            type of action. Use the {@link XDasEventType}.*.{@link XDasEventType#getEventCode()} to fill this.
     * @param xdasStatus
     *            status of action. Use the {@link XdasOutcomes}.*.{@link XdasOutcomes#getOutcomeCode()} to fill this.
     * @param paramsBuf
     *            a CSV string with the action parameters.
     * @throws Exception
     */
    private void pushXdas(final URL source, final URL target, final String user, final String tenant, final int xdasType,
            final int xdasStatus, final String paramsBuf) throws Exception {
        final XdasV1Model model = new XdasV1Model();
        model.setHdr_time_offset( Long.toString( getTimeOffSet() ) );
        model.setHdr_time_zone( "CET" );
        model.setHdr_event_number( Integer.toHexString( xdasType ) );
        model.setHdr_outcome( Integer.toHexString( xdasStatus ) );

        model.setOrg_auth_authority( tenant );
        model.setOrg_principal_name( user );

        model.setOrg_location_name( source.getPath() );
        model.setOrg_location_address( InetAddress.getByName( source.getHost() ).getHostAddress() );
        model.setOrg_service_type( source.getProtocol() );

        if( target != null ) {
            model.setTgt_location_name( target.getPath() );
            model.setTgt_location_address( InetAddress.getByName( target.getHost() ).getHostAddress() );
            model.setTgt_service_type( target.getProtocol() );
        }

        model.setEvt_event_specific_information( paramsBuf );

        sendXdas( model.formatRecord() );
    }
}
