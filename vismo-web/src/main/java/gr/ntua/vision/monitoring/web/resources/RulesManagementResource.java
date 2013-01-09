package gr.ntua.vision.monitoring.web.resources;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
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
     * @return the result of the remove.
     */
    @DELETE
    @Produces("text/plain")
    @Path("rules/{id}")
    public static String RulesConfigurationDelete(@PathParam("id") final Integer id) {
        RulesManagementResource.log.info("removed rule: {}", id, ".");
        return RulesManagementResource.catalog.remove(id);
    }


    /**
     * @param id
     * @return the rule.
     */
    @GET
    @Produces("text/plain")
    @Path("rules/{id}")
    public static String RulesConfigurationGet(@PathParam("id") final Integer id) {
        RulesManagementResource.log.info("requesting rule: {}", id + ".");
        return RulesManagementResource.catalog.get(id);
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
    public static String RulesConfigurationPut(@PathParam("name") final String name, @PathParam("period") final Integer period,
            @PathParam("desc") final String desc) {
        RulesManagementResource.log.info("configuring new rule: {}", name, ".");
        RulesManagementResource.catalog.put(RulesManagementResource.catalog.size() + 1, name + ":" + period + ":" + desc);
        return RulesManagementResource.catalog.get(RulesManagementResource.catalog.size());

    }

}
