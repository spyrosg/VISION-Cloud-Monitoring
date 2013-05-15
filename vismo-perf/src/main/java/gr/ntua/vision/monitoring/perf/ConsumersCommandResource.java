package gr.ntua.vision.monitoring.perf;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * 
 */
@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class ConsumersCommandResource {
    /***/
    final Consumer cons;


    /**
     * Constructor.
     * 
     * @param cons
     */
    public ConsumersCommandResource(final Consumer cons) {
        this.cons = cons;
    }


    /**
     * @return s
     */
    @GET
    @Path("handlers")
    public String getHandlers() {
        return cons.getHandlers();
    }


    /**
     * @param i
     * @return n
     */
    @GET
    @Path("handlers/{index}")
    public Response getNoReceivingEvents(@PathParam("index") final int i) {
        return Response.ok("have received: " + cons.getNoReceivingEvents(i) + " events\n").build();
    }


    /**
     * @param size
     * @return r
     */
    @PUT
    @Path("handlers/{size}")
    public Response newHandler(@PathParam("size") final int size) {
        final int n = cons.registerHandler(size);
        return Response.created(URI.create("/" + n)).build();
    }


    /**
     * @param topic
     * @param size
     * @return r
     */
    @PUT
    @Path("handlers/{topic}/{size}")
    public Response newHandler(@PathParam("topic") final String topic, @PathParam("size") final int size) {
        final int n = cons.registerHandler(topic, size);

        return Response.created(URI.create("/" + n)).build();
    }


    /**
     * @param i
     */
    @POST
    @Path("handlers/{index}")
    public void resetHandler(@PathParam("index") final int i) {
        cons.resetHandler(i);
    }


    /**
     * @return r
     */
    @POST
    @Path("halt")
    public Response shutdown() {
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    cons.halt();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3 * 1000);

        return Response.ok("shutting down\n").build();
    }
}
