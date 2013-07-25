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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
@Path("events")
public class HttpEventResource implements EventSource {
    /***/
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
    private static final Logger                  log       = LoggerFactory.getLogger(HttpEventResource.class);
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
        log.debug("registering listener {}", listener);
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

        if (body == null || body.length() == 0)
            return badRequest("empty event body not allowed");

        try {
            json = (Map<String, Object>) parser.parse(body);
        } catch (final ParseException e) {
            return badRequest("invalid json: " + (e.getMessage() != null ? e.getMessage() : e.toString()));
        }

        try {
            requireField("topic", json);
            requireField("originating-service", json);
            json.put("timestamp", System.currentTimeMillis());
            json.put("originating-machine", req.getRemoteAddr());
            notifyAll(factory.createEvent(JSONObject.toJSONString(json)));
        } catch (final EventValidationError e) {
            return badRequest(e.getMessage());
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
     * @param msg
     * @return the {@link Response}.
     */
    private static Response badRequest(final String msg) {
        return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(msg).build();
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
