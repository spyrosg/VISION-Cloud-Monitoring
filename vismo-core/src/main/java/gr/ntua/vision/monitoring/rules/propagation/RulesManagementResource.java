package gr.ntua.vision.monitoring.rules.propagation;

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
    /***/
    private static RulesPropagationManager        manager;


    /**
     * @param id
     * @return the result of the remove.
     */
    @SuppressWarnings("static-method")
    @DELETE
    @Produces("text/plain")
    @Path("rules/{id}")
    public String RulesConfigurationDelete(@PathParam("id") final Integer id) {
        RulesManagementResource.log.info("removed rule: {}", id, ".");
        final String reply = RulesManagementResource.catalog.remove(id);
        final Message m = new Message();
        m.setGroupSize(RulesManagementResource.manager.getHeartbeatReceiver().getMembers().size());
        m.setCommandId(RulesManagementResource.manager.getRandomID());
        m.setType("multicast");
        m.setToGroup("all");
        m.setContent(RulesManagementResource.catalog);
        RulesManagementResource.manager.getOutQueue().addMessage(m);
        return reply;
    }


    /**
     * @param id
     * @return the rule.
     */
    @SuppressWarnings("static-method")
    @GET
    @Produces("text/plain")
    @Path("rules/{id}")
    public String RulesConfigurationGet(@PathParam("id") final Integer id) {
        RulesManagementResource.log.info("requesting rule: {}", id + ".");
        return RulesManagementResource.catalog.get(id);
    }


    /**
     * @param name
     * @param period
     * @param desc
     * @return the size.
     */
    @SuppressWarnings("static-method")
    @PUT
    @Path("rules/{name}/{period}/{desc}")
    @Consumes("text/plain")
    public String RulesConfigurationPut(@PathParam("name") final String name, @PathParam("period") final Integer period,
            @PathParam("desc") final String desc) {
        RulesManagementResource.log.info("configuring new rule: {}", name, ".");
        RulesManagementResource.catalog.put(RulesManagementResource.catalog.size() + 1, name + ":" + period + ":" + desc);
        final Message m = new Message();
        m.setGroupSize(RulesManagementResource.manager.getHeartbeatReceiver().getMembers().size());
        m.setCommandId(RulesManagementResource.manager.getRandomID());
        m.setType("multicast");
        m.setToGroup("all");
        m.setContent(RulesManagementResource.catalog);
        RulesManagementResource.manager.getOutQueue().addMessage(m);
        return RulesManagementResource.catalog.get(RulesManagementResource.catalog.size());

    }


    /**
     * @param manager
     */
    @SuppressWarnings("static-access")
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
    }

}
