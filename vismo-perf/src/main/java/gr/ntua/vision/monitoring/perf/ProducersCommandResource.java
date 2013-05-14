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
    final Producer      prod;
    /***/
    private final Timer t;


    /**
     * Constructor.
     * 
     * @param prod
     */
    public ProducersCommandResource(final Producer prod) {
        this.t = new Timer(true);
        this.prod = prod;
    }


    /**
     * @param noEvents
     * @param size
     * @return a response.
     */
    @POST
    @Path("events/{no-events}/{size}")
    public Response send(@PathParam("no-events") final int noEvents, @PathParam("size") final long size) {
        if (size < JSON_DIFF)
            return Response.status(Status.BAD_REQUEST).entity("cannot send events of size less of " + JSON_DIFF + " bytes\n")
                    .build();

        t.schedule(new TimerTask() {
            @Override
            public void run() {
                prod.sendEvents(noEvents, size);
            }
        }, 3 * 100);

        return Response.ok("sending " + noEvents + " events\n").build();
    }


    /**
     * @param topic
     * @param noEvents
     * @param size
     * @return a response.
     */
    @POST
    @Path("events/{topic}/{no-events}/{size}")
    public Response send(@PathParam("topic") final String topic, @PathParam("no-events") final int noEvents,
            @PathParam("size") final long size) {

        if (size < JSON_DIFF)
            return Response.status(Status.BAD_REQUEST).entity("cannot send events of size less of " + JSON_DIFF + " bytes\n")
                    .build();

        t.schedule(new TimerTask() {
            @Override
            public void run() {
                prod.sendEvents(topic, noEvents, size);
            }
        }, 3 * 100);

        return Response.ok("sending " + noEvents + " events\n").build();
    }


    /**
     * @return a response.
     */
    @POST
    @Path("halt")
    public Response shutdown() {
        t.schedule(new TimerTask() {
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
