package gr.ntua.vision.monitoring.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * 
 */
@Path("mon")
@Produces(MediaType.APPLICATION_JSON)
public class InternalMetricsResource {
    /**
     * @return a {@link MemoryUsageBean}.
     */
    @SuppressWarnings("static-method")
    @POST
    @Path("mem")
    public MemoryUsageBean memoryConsumption() {
        return MemoryUsageBean.collect();
    }


    /**
     * 
     */
    @SuppressWarnings("static-method")
    @POST
    @Path("gc")
    public void runGC() {
        Runtime.getRuntime().gc();
    }
}
