package gr.ntua.vision.monitoring.perf;

import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * 
 */
@Path("command")
@Produces(MediaType.TEXT_PLAIN)
public class CommandResource {
    /***/
    final Producer      prod;
    /***/
    private final Timer t;


    /**
     * Constructor.
     * 
     * @param prod
     */
    public CommandResource(final Producer prod) {
        this.t = new Timer(true);
        this.prod = prod;
    }


    /**
     * @param noEvents
     * @return a response.
     */
    @POST
    @Path("send/{no-events}")
    public Response send(@PathParam("no-events") final int noEvents) {
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                prod.sendEvents(noEvents);
            }
        }, 3 * 100);

        return Response.ok("sending " + noEvents + " events").build();
    }


    /**
     * @return a response.
     */
    @POST
    @Path("stop")
    public Response stop() {
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
