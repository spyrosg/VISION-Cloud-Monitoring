package gr.ntua.vision.monitoring.resources;

import gr.ntua.vision.monitoring.rules.VismoRule;
import gr.ntua.vision.monitoring.rules.VismoRulesFactory;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


/**
 * 
 */
@Path("rules")
@Consumes(MediaType.APPLICATION_JSON)
public class RulesResource {
    /***/
    private final VismoRulesFactory factory;


    /**
     * Constructor.
     * 
     * @param factory
     */
    public RulesResource(final VismoRulesFactory factory) {
        this.factory = factory;
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
    public Response submitRule(final ThresholdRuleBean bean) {
        try {
            final VismoRule rule = factory.buildRule(bean);

            rule.submit();

            return Response.created(URI.create("/" + rule.id())).type(MediaType.TEXT_PLAIN).entity(rule.id()).build();
        } catch (final ThresholdRuleValidationError e) {
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
                    .entity("invalid rule specification: " + e.getMessage()).build());
        }
    }
}
