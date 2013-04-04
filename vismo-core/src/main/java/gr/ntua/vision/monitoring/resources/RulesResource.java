package gr.ntua.vision.monitoring.resources;

import gr.ntua.vision.monitoring.rules.DefaultRuleBean;
import gr.ntua.vision.monitoring.rules.RuleOperation;
import gr.ntua.vision.monitoring.rules.RulesFactory;
import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.VismoRule;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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
    /***/
    private final RulesFactory factory;
    /***/
    private final RulesStore   store;


    /**
     * Constructor.
     * 
     * @param factory
     * @param store
     */
    public RulesResource(final RulesFactory factory, final RulesStore store) {
        this.factory = factory;
        this.store = store;
    }


    /**
     * @param id
     * @return if the rule was succesfully removed from the store, a respone of status 204, else, a response of status 404.
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
    @Produces(MediaType.TEXT_PLAIN)
    public Response listRules() {
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
     * @param name
     * @param period
     * @return on success, return the id of the newly added rule
     */
    @Path("{name}/{period}")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response submitDefaultRule(@PathParam("name") final String name, @PathParam("period") final long period) {
        final VismoRule rule = factory.buildFrom(new DefaultRuleBean(name, period));

        if (rule == null)
            return Response.status(Status.NOT_FOUND).entity("unknown rule: " + rule).build();

        rule.submit();

        return Response.created(URI.create("/" + rule.id())).entity(rule.id()).build();
    }


    /**
     * Construct and submit a new rule to the engine.
     * 
     * @param bean
     * @return on success, return the id of the newly added rule
     * @throws WebApplicationException
     *             when a rule is not found or the
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response submitThresholdRule(final ThresholdRuleBean bean) {
        try {
            final VismoRule rule = factory.buildFrom(bean);

            rule.submit();

            return Response.created(URI.create("/" + rule.id())).entity(rule.id()).build();
        } catch (final ThresholdRuleValidationError e) {
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
                    .entity("invalid rule specification: " + e.getMessage()).build());
        }
    }
}
