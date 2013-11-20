package gr.ntua.vision.monitoring.resources;

import gr.ntua.vision.monitoring.VMInfo;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;


/**
 * 
 */
@Path("version")
public class VersionResource {
    /***/
    private String       corsHeaders;
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
     * This OPTIONS request/response is necessary if you consumes other format than text/plain or if you use other HTTP verbs than
     * GET and POST
     * 
     * @param accessControlRequestHeaders
     * @return an 200 response.
     */
    @OPTIONS
    public Response corsResource(@HeaderParam("Access-Control-Request-Headers") final String accessControlRequestHeaders) {
        corsHeaders = accessControlRequestHeaders;
        return toCORS(Response.ok(), accessControlRequestHeaders);
    }


    /**
     * @return the system version as a string.
     */
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response versionString() {
        return toCORS(Response.ok(vminfo.getVersion()));
    }


    /**
     * @param req
     * @return CORS-ed a response object.
     */
    private Response toCORS(final ResponseBuilder req) {
        return toCORS(req, corsHeaders);
    }


    /**
     * @param req
     * @param returnMethod
     * @return a cors-ed response object.
     */
    private static Response toCORS(final ResponseBuilder req, final String returnMethod) {
        final ResponseBuilder rb = req.header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods",
                                                                                         "GET, POST, PUT, DELETE, OPTIONS");

        if (returnMethod != null && !returnMethod.isEmpty())
            rb.header("Access-Control-Allow-Headers", returnMethod);

        return rb.build();
    }
}
