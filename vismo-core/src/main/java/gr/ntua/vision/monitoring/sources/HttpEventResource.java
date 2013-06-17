package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.MonitoringEvent;


import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


/**
 * 
 */
/**
 * @author makis
 *
 */
@Path("events")
public class HttpEventResource implements EventSource {
    /***/
    private final EventFactory                   factory;
    /** the listeners lists. */
    private final ArrayList<EventSourceListener> listeners = new ArrayList<EventSourceListener>();
    


    /**
     * Constructor
     * 
     * @param factory
     */
    public HttpEventResource(final EventFactory factory) {
        this.factory = factory;
    }


    /**
     * @see gr.ntua.vision.monitoring.sources.EventSource#add(gr.ntua.vision.monitoring.sources.EventSourceListener)
     */
    @Override
    public void add(final EventSourceListener listener) {
        listeners.add(listener);
    }


    /**
     * @param body
     * @return the response.
     */
    @PUT
    public Response putEvent(final String body) {
        try {
            final MonitoringEvent monev = factory.createEvent(body);
            //notify listeners of the event
            notifyAll(monev);

            eventValidation(monev);
        } catch (final java.lang.Error e) {
            return Response.status(400).entity(e.getMessage()).build();
        }

        return Response.created(URI.create("/")).build();
    }
    
    /**
     * Notify any listeners of the incoming message.
     * 
     * @param e
     *            the event received.
     */
    private void notifyAll(final MonitoringEvent e) {
        for (final EventSourceListener listener : listeners)
            listener.receive(e);
    }

    
    


    /**
     * Validate event's fields
     * 
     * @param ev
     * @return the response.
     */
    private static Response eventValidation(final MonitoringEvent ev) {
        validateIP(ev);
        validateOriginatingService(ev);
        validateEventTimestamp(ev);
        validateEventTopic(ev);

        return Response.created(URI.create("/")).build();
    }


    /**
     * Validate Event's Timestamp
     * 
     * @param ev
     * @return the response.
     */
    private static Response validateEventTimestamp(final MonitoringEvent ev) {
        final Long timest = ev.timestamp();
        if (timest == null)
            return Response.status(400).entity("field timestamp required").build();

        return Response.created(URI.create("/")).build();
    }


    /**
     * Validate Event's topic
     * 
     * @param ev
     * @return the response.
     */
    private static Response validateEventTopic(final MonitoringEvent ev) {
        final String topic = ev.topic();
        if (topic == null)
            return Response.status(400).entity("field topic required").build();

        return Response.created(URI.create("/")).build();
    }


    /**
     * Validating Event IP
     * 
     * @param ev
     * @return the response.
     */
    private static Response validateIP(final MonitoringEvent ev) {
        try {
            final InetAddress IP = ev.originatingIP();
            if (IP == null)
                return Response.status(400).entity("No originating IP").build();
        } catch (final UnknownHostException e) {
            return Response.status(400).entity(e.getMessage()).build();
        }

        return Response.created(URI.create("/")).build();
    }


    /**
     * Validate Event's Originating Service
     * 
     * @param ev
     * @return the response.
     */
    private static Response validateOriginatingService(final MonitoringEvent ev) {
        final String service = ev.originatingService();
        if (service == null)
            return Response.status(400).entity("field originating-service required").build();

        return Response.created(URI.create("/")).build();
    }
}
