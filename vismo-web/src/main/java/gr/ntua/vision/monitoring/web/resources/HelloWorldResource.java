package gr.ntua.vision.monitoring.web.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;


/**
 * 
 */
@Path("/hello")
public class HelloWorldResource {
    /**
     * @return
     */
    @GET
    public String say() {
        return "hello world";
    }
}
