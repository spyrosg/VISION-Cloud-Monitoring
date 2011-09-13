package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.Event.EventType;
import gr.ntua.vision.monitoring.model.Location;
import gr.ntua.vision.monitoring.model.Resource;
import gr.ntua.vision.monitoring.model.impl.EventImpl;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.json.JSONException;

import com.google.common.collect.Lists;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.representation.Form;


/**
 * The monitoring library singleton.
 */
public class VisionMonitoring
{
	/** the client used to make HTTP requests. */
	private static final Client		client	= Client.create();
	/** the single object's instance. */
	private static VisionMonitoring	instance;
	/** the monitoring URL */
	private final String			url;
	/** the id of this component. */
	private final UUID				id;

	static
	{
		client.setConnectTimeout( 1000 );
	}


	/**
	 * c/tor.
	 * 
	 * @param url
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
	 */
	public static VisionMonitoring initialize(String url, UUID id)
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
	 * log an action.
	 * 
	 * @param action
	 *            the action description.
	 * @param resources
	 *            the consumed resources.
	 * @param source
	 *            the action source.
	 * @param target
	 *            the action target.
	 */
	public void log(String action, Collection<Resource> resources, Location source, Location target)
	{
		long tm = new Date().getTime();
		EventImpl event = new EventImpl( null, id, action, EventType.Action, Lists.newArrayList( resources ), tm, tm, source,
				target, null );
		push( event );
	}


	/**
	 * push an event to the monitoring system.
	 * 
	 * @param event
	 *            the event to push.
	 */
	public void push(Event event)
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
