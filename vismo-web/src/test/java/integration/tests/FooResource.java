package integration.tests;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;


/**
 * 
 */
@Path("foo")
@Consumes(MediaType.TEXT_PLAIN)
public class FooResource {
    /***/
    private final ArrayList<String> values = new ArrayList<String>();


    /**
     * Constructor.
     * 
     * @param s
     */
    public FooResource(final String s) {
        this.values.add(s);
    }


    /**
     * @param value
     */
    @PUT
    @Path("{val}")
    public void append(@PathParam("val") final String value) {
        values.add(value);
    }


    /**
     * @param index
     * @return s
     */
    @GET
    @Path("{i}")
    public String getByIndex(@PathParam("i") final int index) {
        return values.get(index);
    }
}
