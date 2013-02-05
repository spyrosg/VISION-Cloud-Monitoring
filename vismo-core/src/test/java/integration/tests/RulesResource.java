package integration.tests;

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
 * 
 */
@Path("rules")
public class RulesResource {
    /***/
    private static final Logger            log = LoggerFactory.getLogger(RulesResource.class);
    /***/
    private final HashMap<Integer, String> catalog;


    /**
     * Constructor.
     * 
     * @param catalog
     */
    public RulesResource(final HashMap<Integer, String> catalog) {
        this.catalog = catalog;
    }


    /**
     * @param id
     * @return the result of the remove.
     */
    @DELETE
    @Produces("text/plain")
    @Path("{id}")
    public String RulesConfigurationDelete(@PathParam("id") final Integer id) {
        log.info("removed rule: {}", id, ".");

        return catalog.remove(id);
    }


    /**
     * @param id
     * @return the rule.
     */
    @GET
    @Produces("text/plain")
    @Path("{id}")
    public String RulesConfigurationGet(@PathParam("id") final Integer id) {
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
    @Path("{name}/{period}/{desc}")
    @Consumes("text/plain")
    public String RulesConfigurationPut(@PathParam("name") final String name, @PathParam("period") final Integer period,
            @PathParam("desc") final String desc) {
        log.info("configuring new rule: {}", name, ".");
        catalog.put(catalog.size() + 1, name + ":" + period + ":" + desc);

        return catalog.get(catalog.size());
    }
}
