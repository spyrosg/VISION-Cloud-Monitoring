package gr.ntua.vision.monitoring.resources;

import gr.ntua.vision.monitoring.VMInfo;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * 
 */
@Path("version")
public class VersionResource {
    /***/
    private final VMInfo vminfo;


    /**
     * Constructor.
     * 
     * @param vminfo
     */
    public VersionResource(final VMInfo vminfo) {
        this.vminfo = vminfo;
    }


    /**
     * @return the system version as a string.
     */
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String versionString() {
        return vminfo.getVersion();
    }
}