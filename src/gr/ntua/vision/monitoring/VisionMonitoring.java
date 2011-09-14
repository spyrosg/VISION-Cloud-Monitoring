package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.Event.EventType;
import gr.ntua.vision.monitoring.model.Location;
import gr.ntua.vision.monitoring.model.Resource;
import gr.ntua.vision.monitoring.model.impl.EventImpl;
import gr.ntua.vision.monitoring.model.impl.LocationImpl;
import it.eng.compliance.persistenceservice.model.XdasV1Model;
import it.eng.compliance.xdas.parser.XDasEventType;
import it.eng.compliance.xdas.parser.XdasOutcomes;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.jms.JMSException;

import org.json.JSONException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.representation.Form;


/**
 * The monitoring library singleton.
 */
public class VisionMonitoring extends XdasPublisher
{
	/** the client used to make HTTP requests. */
	private static final Client				client			= Client.create();
	/** the single object's instance. */
	private static VisionMonitoring			instance;
	/** the monitoring URL */
	private final String					url;
	/** the id of this component. */
	private final UUID						id;

	/** empty parameters */
	public static final Map<String, String>	EmptyParameters	= Collections.unmodifiableMap( Maps.<String, String> newHashMap() );
	/** empty resource list. */
	public static final List<Resource>		EmptyResources	= Collections.unmodifiableList( Lists.<Resource> newArrayList() );

	static
	{
		client.setConnectTimeout( 1000 );
	}


	/**
	 * c/tor.
	 * 
	 * @param url
	 * @param id
	 */
	private VisionMonitoring(String url, UUID id)
	{
		this.url = url;
		this.id = id;
	}


	/**
	 * initialize the single instance.
	 * 
	 * @param url
	 *            the cluster monitoring URL.
	 * @param id
	 *            the hot component's ID.
	 * @return the instance created.
	 * @throws JMSException
	 */
	public static VisionMonitoring initialize(String url, UUID id) throws JMSException
	{
		return instance = new VisionMonitoring( url, id );
	}


	/**
	 * get the single monitoring instance.
	 * 
	 * @return the instance.
	 */
	public static VisionMonitoring instance()
	{
		return instance;
	}


	/**
	 * log an action
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
	 * @param parameters
	 *            parameters of action (action specific data).
	 * @param resources
	 *            the list with all resources that are consumed by te logged action.
	 * @throws Exception
	 */
	public void log(URL source, URL target, String user, String tenant, int xdasType, int xdasStatus,
			Map<String, String> parameters, List<Resource> resources) throws Exception
	{
		StringBuilder buf = new StringBuilder();
		for( Map.Entry<String, String> prm : parameters.entrySet() )
		{
			if( buf.length() > 0 ) buf.append( ',' );
			buf.append( prm.getKey() );
			buf.append( '=' );
			buf.append( prm.getValue() );
		}
		String paramsBuf = buf.toString();

		pushXdas( source, target, user, tenant, xdasType, xdasStatus, paramsBuf );
		pushEvent( source, target, user, tenant, resources, paramsBuf );
	}


	/**
	 * generate and push the event that is emitted by the given data.
	 * 
	 * @param source
	 *            action source
	 * @param target
	 *            action target. It may be <code>null</code>.
	 * @param user
	 *            user performing the action
	 * @param tenant
	 *            tenant performing the action
	 * @param resources
	 *            the list with all resources that are consumed by the logged action.
	 * @param paramsBuf
	 *            a CSV string with the action parameters.
	 * @throws UnknownHostException
	 */
	private void pushEvent(URL source, URL target, String user, String tenant, List<Resource> resources, String paramsBuf)
			throws UnknownHostException
	{
		long now = new Date().getTime();
		Location src = new LocationImpl( source.getHost(), source.getPath(), user, tenant, InetAddress
				.getByName( source.getHost() ).getHostAddress() );
		Location trg = null;
		if( target != null ) //
			trg = new LocationImpl( target.getHost(), target.getPath(), user, tenant, InetAddress.getByName( target.getHost() )
					.getHostAddress() );

		String description = String.format( "%s#%s/%s[%s]@%s", source, user, tenant, paramsBuf.toString(), target );

		Event event = new EventImpl( null, id, description, EventType.Action, resources, now, now, src, trg, null );

		push( event );
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
	private void pushXdas(URL source, URL target, String user, String tenant, int xdasType, int xdasStatus, String paramsBuf)
			throws Exception
	{
		XdasV1Model model = new XdasV1Model();
		model.setHdr_time_offset( Long.toString( getTimeOffSet() ) );
		model.setHdr_time_zone( "CET" );
		model.setHdr_event_number( Integer.toHexString( xdasType ) );
		model.setHdr_outcome( Integer.toHexString( xdasStatus ) );

		model.setOrg_auth_authority( tenant );
		model.setOrg_principal_name( user );

		model.setOrg_location_name( source.getPath() );
		model.setOrg_location_address( InetAddress.getByName( source.getHost() ).getHostAddress() );
		model.setOrg_service_type( source.getProtocol() );

		if( target != null )
		{
			model.setTgt_location_name( target.getPath() );
			model.setTgt_location_address( InetAddress.getByName( target.getHost() ).getHostAddress() );
			model.setTgt_service_type( target.getProtocol() );
		}

		model.setEvt_event_specific_information( paramsBuf );

		sendXdas( model.formatRecord() );
	}


	/**
	 * get the time zone string.
	 * 
	 * @return the string.
	 */
	@SuppressWarnings("unused")
	private String getStrTimeZone()
	{
		TimeZone timeZone = TimeZone.getDefault();

		int iOffsetSeconds = -( timeZone.getRawOffset() / 1000 );

		int iOffsetHours = iOffsetSeconds / 3600;
		int iOffsetMinutes = iOffsetSeconds % 3600 / 60;
		StringBuffer strTimeZone = new StringBuffer();
		strTimeZone.append( timeZone.getDisplayName( false, 0 ) );
		strTimeZone.append( iOffsetHours );
		if( iOffsetMinutes != 0 )
		{
			strTimeZone.append( "%:" );
			strTimeZone.append( iOffsetMinutes );
		}

		if( timeZone.useDaylightTime() )
		{
			strTimeZone.append( timeZone.getDisplayName( true, 0 ) );
		}
		return strTimeZone.toString();
	}


	/**
	 * get the time offset string.
	 * 
	 * @return the string.
	 */
	private long getTimeOffSet()
	{
		Date date = new Date();
		TimeZone cetTime = TimeZone.getTimeZone( "CET" );
		DateFormat cetFormat = new SimpleDateFormat();
		cetFormat.setTimeZone( cetTime );
		return date.getTime() / 1000L;
	}


	/**
	 * push an event to the monitoring system.
	 * 
	 * @param event
	 *            the event to push.
	 */
	private void push(Event event)
	{
		try
		{
			Form form = new Form();
			form.add( "event", event.toJSON().toString() );

			client.resource( url ).post( form );
		}
		catch( JSONException x )
		{
			x.printStackTrace();
		}
	}
}
