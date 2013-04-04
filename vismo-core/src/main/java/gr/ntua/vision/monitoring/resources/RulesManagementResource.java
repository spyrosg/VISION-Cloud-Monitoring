package gr.ntua.vision.monitoring.resources;

import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.rules.propagation.message.MessageFactory;
import gr.ntua.vision.monitoring.rules.propagation.message.MessageType;

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
    private static final Logger           log = LoggerFactory.getLogger(RulesManagementResource.class);
    /***/
    private final RulesPropagationManager manager;
    /***/
    private final MessageFactory          messageFactory;


    /**
     * Constructor.
     * 
     * @param manager
     */
    public RulesManagementResource(final RulesPropagationManager manager) {
        this.manager = manager;
        messageFactory = new MessageFactory(manager);

    }


    /**
     * @param id
     * @return rule to be removed.
     */
    @DELETE
    @Produces("text/plain")
    @Path("rules/{id}")
    public String RulesConfigurationDelete(@PathParam("id") final Integer id) {
        if (manager.getRuleStore().getRule(id) != null) {
            RulesManagementResource.log.info("removing rule: {}", id, ".");
            manager.getOutQueue().addMessage(messageFactory.createMessage(MessageType.DELETE_RULE, id));
        }
        return "removing: " + id;
    }


    /**
     * @param id
     * @return the rule.
     */
    @GET
    @Produces("text/plain")
    @Path("rules/{id}")
    public String RulesConfigurationGet(@PathParam("id") final Integer id) {
        RulesManagementResource.log.info("requesting rule: {}", id + ".");
        return manager.getRuleStore().getRule(id);
    }


    /**
     * @return the rule.
     */
    @GET
    @Produces("text/plain")
    @Path("rules/all")
    public String RulesConfigurationGetAll() {
        RulesManagementResource.log.info("requesting all rules");
        return manager.getRuleStore().getRules();
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
        if (checkValidRule(name) && !manager.getRuleStore().containsRule(name + ":" + period + ":" + desc)) {
            commandId = manager.getRandomID();
            RulesManagementResource.log.info("configuring new rule: {}", name, ".");
            manager.getOutQueue().addMessage(messageFactory.createMessage(MessageType.ADD_RULE, commandId, name + ":" + period
                                                     + ":" + desc));
        }
        return "adding rule: " + commandId + " " + name + ":" + period + ":" + desc;
    }


    /**
     * checks the validity of the rule name
     * 
     * @param name
     * @return true/false
     */
    private boolean checkValidRule(final String name) {
        if (name.equals("TestingRule") || name.equals("PassThroughRule") || name.equals("AccountingRule")
                || name.equals("CTORule"))
            return true;
        return false;
    }

}
