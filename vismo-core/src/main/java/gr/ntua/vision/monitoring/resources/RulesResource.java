package gr.ntua.vision.monitoring.resources;

import gr.ntua.vision.monitoring.rules.RuleOperation;
import gr.ntua.vision.monitoring.rules.RulesFactory;
import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.VismoRule;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


/**
 * 
 */
@Path("rules")
public class RulesResource {
    /** this header is used to signify that the rule was passed to us by another instance and not by the user. */
    static final String        X_VISION_INTERCHANGE_HEADER = "x-vision-interchange";
    /***/
    private final RulesFactory factory;
    /***/
    private final RulesStore   store;
    /***/
    private final RulesUpdate  update;


    /**
     * Constructor.
     * 
     * @param defaultPort
     * @param factory
     * @param store
     */
    public RulesResource(final int defaultPort, final RulesFactory factory, final RulesStore store) {
        this.factory = factory;
        this.store = store;
        this.update = new RulesUpdate(defaultPort);
    }


    /**
     * @param id
     * @return if the rule was successfully removed from the store, a response of status 204, else, a response of status 404.
     */
    @Path("{rule-id}")
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteRule(@PathParam("rule-id") final String id) {
        return (store.remove(id) ? Response.noContent() : Response.status(Status.NOT_FOUND)).build();
    }


    /**
     * @return a response of status 200.
     */
    @GET
    @Produces("application/json; qs=0.9")
    public List<RuleIdBean> listRulesAsJSON() {
        final ArrayList<RuleIdBean> rules = new ArrayList<RuleIdBean>();

        store.forEach(new RuleOperation() {
            @Override
            public void run(final VismoRule rule) {
                rules.add(new RuleIdBean(rule.id(), rule.getClass().getSimpleName()));
            }
        });

        return rules;
    }


    /**
     * @return a response of status 200.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response listRulesAsText() {
        final StringBuilder buf = new StringBuilder();

        store.forEach(new RuleOperation() {
            @Override
            public void run(final VismoRule rule) {
                buf.append(rule.toString());
                buf.append(": ");
                buf.append(rule.id());
                buf.append("\n");
            }
        });

        return Response.ok(buf.toString()).build();
    }


    /**
     * @param isInterchange
     * @param name
     * @param period
     * @return on success, return the id of the newly added rule
     */
    @Path("{name}/{period}")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response submitDefaultRule(@HeaderParam(X_VISION_INTERCHANGE_HEADER) final boolean isInterchange,
            @PathParam("name") final String name, @PathParam("period") final long period) {
        final DefaultRuleBean bean = new DefaultRuleBean(name, period);
        final VismoRule rule = factory.buildFrom(bean);

        if (rule == null)
            return badSpecification("unknown rule name: " + name);

        rule.submit();
        pushRule(isInterchange, rule.id(), bean);

        return submittedSuccessfully(rule);
    }


    /**
     * Construct and submit a new rule to the engine.
     * 
     * @param isInterchange
     * @param bean
     * @return on success, return the id of the newly added rule
     * @throws WebApplicationException
     *             when a rule is not found or the
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response submitThresholdRule(@HeaderParam(X_VISION_INTERCHANGE_HEADER) final boolean isInterchange,
            final ThresholdRuleBean bean) {
        try {
            final VismoRule rule = factory.buildFrom(bean);

            rule.submit();
            pushRule(isInterchange, rule.id(), bean);

            return submittedSuccessfully(rule);
        } catch (final ThresholdRuleValidationError e) {
            return badSpecification(e.getMessage());
        }
    }


    /**
     * Push the new rule to all other known nodes. We do this here, in the controller layer, since there isn't a good or general
     * enough rules representation in the domain. VismoRulesEngine knows only of VismoRule instances.
     * 
     * @param isInterchange
     * @param id
     * @param bean
     */
    private void pushRule(final boolean isInterchange, final String id, final RuleBean bean) {
        update.push(isInterchange, id, bean);
    }


    /**
     * @param msg
     * @return the {@link Response} object.
     */
    private static Response badSpecification(final String msg) {
        return Response.status(Status.BAD_REQUEST).entity(msg).build();
    }


    /**
     * @param rule
     * @return the {@link Response} object.
     */
    private static Response submittedSuccessfully(final VismoRule rule) {
        return Response.created(URI.create("/" + rule.id())).entity(rule.id()).build();
    }
}
