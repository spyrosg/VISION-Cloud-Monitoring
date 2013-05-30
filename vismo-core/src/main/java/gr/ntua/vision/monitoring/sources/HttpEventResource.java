package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.events.VismoEventFactory;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


/**
 * 
 */
@Path("events")
public class HttpEventResource implements EventSource {
    /*
     * TODO: pass factory to the constructor
     * TODO: implement {@link #add(EventSourceListener)}.
     * TODO: move event validation to other method(s).
     */

    /***/
    private final EventFactory factory = new VismoEventFactory();


    /**
     * @see gr.ntua.vision.monitoring.sources.EventSource#add(gr.ntua.vision.monitoring.sources.EventSourceListener)
     */
    @Override
    public void add(final EventSourceListener listener) {
        // TODO Auto-generated method stub
    }


    /**
     * @param body
     * @return
     */
    @PUT
    public Response putEvent(final String body) {
        try {
            final MonitoringEvent monev = factory.createEvent(body);

            try {
                final InetAddress IP = monev.originatingIP();
                if (IP == null)
                    return Response.status(400).entity("No originating IP").build();
            } catch (final UnknownHostException e) {
                return Response.status(400).entity(e.getMessage()).build();
            }
            final String service = monev.originatingService();
            if (service == null)
                return Response.status(400).entity("field originating-service required").build();
            final Long timest = monev.timestamp();
            if (timest == null)
                return Response.status(400).entity("field timestamp required").build();
            final String topic = monev.topic();
            if (topic == null)
                return Response.status(400).entity("field topic required").build();
        } catch (final java.lang.Error e) {
            return Response.status(400).entity(e.getMessage()).build();
        }

        return Response.created(URI.create("/")).build();
    }
}
