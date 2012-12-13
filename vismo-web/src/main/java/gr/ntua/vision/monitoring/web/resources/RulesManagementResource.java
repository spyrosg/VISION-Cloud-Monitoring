package gr.ntua.vision.monitoring.web.resources;

import java.util.HashMap;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 *
 */
@Path("/")
public class RulesManagementResource {
    /**
     * 
     */
    private static final HashMap<Integer, String> catalog = new HashMap<Integer, String>();
    /**
     * 
     */
    private static final Logger                   log     = LoggerFactory.getLogger(RulesManagementResource.class);



    /**
     * @param id
     * @return the rule.
     */
    @GET
    @Produces("text/plain")
    @Path("rules/{id}")
    public static String RulesConfigurationGet(@PathParam("id") Integer id) {
        log.info("requesting rule: {}", id + ".");
        return catalog.get(id);
    }


    /**
     * @param name
     * @param period
     * @param desc
     * @return the size.
     */
    @PUT
    @Path("rules/{name}/{period}/{desc}")
    @Consumes("text/plain")
    public static String RulesConfigurationPut(@PathParam("name") String name, @PathParam("period") Integer period,
            @PathParam("desc") String desc) {
        log.info("configuring new rule: {}", name, ".");
        catalog.put(catalog.size() + 1, name + ":" + period + ":" + desc);
        return catalog.get(catalog.size());

    }


    /**
     * @param id
     * @return the result of the remove.
     */
    @DELETE
    @Produces("text/plain")
    @Path("rules/{id}")
    public static String RulesConfigurationDelete(@PathParam("id") Integer id) {
        log.info("removed rule: {}", id, ".");
        return catalog.remove(id);
    }

}
