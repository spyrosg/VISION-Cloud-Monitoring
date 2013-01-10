package integration.tests;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;


/**
 * 
 */
@Path("foo")
@Consumes(MediaType.TEXT_PLAIN)
public class FooResource {
    /***/
    private final String s;


    /**
     * Constructor.
     * 
     * @param s
     */
    public FooResource(final String s) {
        this.s = s;
    }


    /**
     * @return s
     */
    @GET
    public String get() {
        return s;
    }
}
