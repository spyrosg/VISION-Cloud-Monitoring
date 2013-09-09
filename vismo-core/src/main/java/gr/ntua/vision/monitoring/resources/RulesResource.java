package gr.ntua.vision.monitoring.resources;

import gr.ntua.vision.monitoring.rules.RuleOperation;
import gr.ntua.vision.monitoring.rules.RulesFactory;
import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.VismoRule;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
     * @param isInterchange
     * @param id
     * @return if the rule was successfully removed from the store, a response of status 204, else, a response of status 404.
     */
    @Path("{rule-id}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteRule(@HeaderParam(X_VISION_INTERCHANGE_HEADER) final boolean isInterchange,
            @PathParam("rule-id") final String id) {

        if (store.remove(id)) {
            update.notifyDeletion(isInterchange, id);
            return Response.noContent().build();
        }

        return Response.status(Status.NOT_FOUND).build();
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
        update.notifyInsertion(isInterchange, rule.id(), bean);

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
            update.notifyInsertion(isInterchange, rule.id(), bean);

            return submittedSuccessfully(rule);
        } catch (final ThresholdRuleValidationError e) {
            return badSpecification(e.getMessage());
        }
    }


    /**
     * @param id
     * @param fieldName
     * @param value
     * @return 201 when the update was successful, 404, when no rule corresponds to given id or 400 when the update cannot be
     *         performed.
     */
    @PUT
    @Path("{rule-id}/{field}/{value}")
    public Response updateRule(@PathParam("rule-id") final String id, @PathParam("field") final String fieldName,
            @PathParam("value") final String value) {
        // TODO: push delete rule
        try {
            store.update(id, fieldName, value);
        } catch (final NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (final IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        return Response.noContent().build();
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
