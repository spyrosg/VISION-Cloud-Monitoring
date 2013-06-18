package gr.ntua.vision.monitoring.resources;

import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.sources.EventSource;
import gr.ntua.vision.monitoring.sources.EventSourceListener;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * 
 */
@Path("events")
public class HttpEventResource implements EventSource {
    /**
     * 
     */
    @SuppressWarnings("serial")
    private static class EventValidationError extends RuntimeException {
        /**
         * Constructor.
         * 
         * @param message
         */
        public EventValidationError(final String message) {
            super(message);
        }
    }
    /***/
    private final EventFactory                   factory;
    /** the listeners lists. */
    private final ArrayList<EventSourceListener> listeners = new ArrayList<EventSourceListener>();

    /***/
    private final JSONParser                     parser;


    /**
     * Constructor.
     */
    public HttpEventResource() {
        this(new VismoEventFactory());
    }


    /**
     * Constructor
     * 
     * @param factory
     */
    public HttpEventResource(final EventFactory factory) {
        this.factory = factory;
        this.parser = new JSONParser();
    }


    /**
     * @see gr.ntua.vision.monitoring.sources.EventSource#add(gr.ntua.vision.monitoring.sources.EventSourceListener)
     */
    @Override
    public void add(final EventSourceListener listener) {
        listeners.add(listener);
    }


    /**
     * @param req
     * @param body
     * @return the response.
     */
    @SuppressWarnings("unchecked")
    @PUT
    public Response putEvent(final @Context HttpServletRequest req, final String body) {
        final Map<String, Object> json;

        try {
            json = (Map<String, Object>) parser.parse(body);
        } catch (final ParseException e) {
            return Response.status(400).entity(e.getMessage()).build();
        }

        try {
            requireField("topic", json);
            requireField("originating-service", json);
            json.put("timestamp", System.currentTimeMillis());
            json.put("originating-machine", req.getRemoteAddr());
            notifyAll(factory.createEvent(JSONObject.toJSONString(json)));
        } catch (final EventValidationError e) {
            return badRequest(e);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
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
     * @param e
     * @return the {@link Response}.
     */
    private static Response badRequest(final EventValidationError e) {
        return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
    }


    /**
     * Assert that the require field is not missing from the map.
     * 
     * @param field
     * @param map
     */
    private static void requireField(final String field, final Map<String, Object> map) {
        if (!map.containsKey(field))
            throw new EventValidationError("field '" + field + "' is required");
    }
}
