package gr.ntua.vision.monitoring.perf;

import static gr.ntua.vision.monitoring.perf.ConstantSizeEventService.JSON_DIFF;

import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


/**
 * 
 */
@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class ProducersCommandResource {
    /***/
    final Producer prod;


    /**
     * Constructor.
     * 
     * @param prod
     */
    public ProducersCommandResource(final Producer prod) {
        this.prod = prod;
    }


    /**
     * @param topic
     * @param rate
     * @param noEvents
     * @param size
     * @return a response.
     */
    @POST
    @Path("events/{topic}/{rate}/{no-events}/{size}")
    public Response send(@PathParam("topic") final String topic, @PathParam("rate") final double rate,
            @PathParam("no-events") final int noEvents, @PathParam("size") final long size) {
        if (size < JSON_DIFF)
            return Response.status(Status.BAD_REQUEST).entity("cannot send events of size less of " + JSON_DIFF + " bytes\n")
                    .build();

        final long start = System.currentTimeMillis();
        final int actualNoSent = prod.sendEvents(topic, rate, noEvents, size);
        final double dur = (System.currentTimeMillis() - start) / 1000.0;

        return Response.ok("sent " + actualNoSent + " events of size " + size + " bytes in " + dur + " seconds (" + noEvents
                                   / dur + " ev/sec)\n").build();
    }


    /**
     * @return a response.
     */
    @POST
    @Path("halt")
    public Response shutdown() {
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    prod.halt();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3 * 1000);

        return Response.ok("shutting down\n").build();
    }
}
