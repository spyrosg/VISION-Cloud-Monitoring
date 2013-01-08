package gr.ntua.vision.monitoring.rules.propagation;

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
    /***/
    private static final Logger            log = LoggerFactory.getLogger(RulesManagementResource.class);
    /***/
    private static RulesPropagationManager manager;


    /**
     * @param id
     * @return rule to be removed.
     */
    @SuppressWarnings("static-method")
    @DELETE
    @Produces("text/plain")
    @Path("rules/{id}")
    public String RulesConfigurationDelete(@PathParam("id") final Integer id) {
        if (RulesManagementResource.manager.getRuleStore().getRule(id) != null) {
            RulesManagementResource.log.info("removing rule: {}", id, ".");
            final int commandId = RulesManagementResource.manager.getRandomID();
            final Message m = new Message();
            m.setGroupSize(RulesManagementResource.manager.getHeartbeatReceiver().getMembers().size());
            m.setCommandId(commandId);
            m.setType("del");
            m.setCommand(RulesManagementResource.manager.getRuleStore().getRule(id));
            RulesManagementResource.manager.getOutQueue().addMessage(m);
        }
        return "removing: " + id;
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
        return RulesManagementResource.manager.getRuleStore().getRule(id);
    }


    /**
     * @param id
     * @return the rule.
     */
    @SuppressWarnings("static-method")
    @GET
    @Produces("text/plain")
    @Path("rules/all")
    public String RulesConfigurationGetAll(@PathParam("id") final Integer id) {
        RulesManagementResource.log.info("requesting rule: {}", id + ".");
        return RulesManagementResource.manager.getRuleStore().getRules();
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
    public String RulesConfigurationPut(@PathParam("name") final String name, @PathParam("period") final Integer period,
            @PathParam("desc") final String desc) {
        int commandId = 0;
        if (checkValidRule(name)
                && !RulesManagementResource.manager.getRuleStore().containsRule(name + ":" + period + ":" + desc)) {
            commandId = RulesManagementResource.manager.getRandomID();
            RulesManagementResource.log.info("configuring new rule: {}", name, ".");
            final Message m = new Message();
            m.setGroupSize(RulesManagementResource.manager.getHeartbeatReceiver().getMembers().size());
            m.setCommandId(commandId);
            m.setType("add");
            m.setCommand(name + ":" + period + ":" + desc);
            RulesManagementResource.manager.getOutQueue().addMessage(m);
        }
        return "adding rule: " + commandId + " " + name + ":" + period + ":" + desc;
    }


    /**
     * @param manager
     */
    @SuppressWarnings("static-access")
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
    }


    /**
     * checks the validity of the rule name
     * 
     * @param name
     * @return true/false
     */
    @SuppressWarnings("static-method")
    private boolean checkValidRule(final String name) {
        if (name.equals("TestingRule") || name.equals("PassThroughRule") || name.equals("AccountingRule")
                || name.equals("CTORule"))
            return true;
        return false;
    }

}
