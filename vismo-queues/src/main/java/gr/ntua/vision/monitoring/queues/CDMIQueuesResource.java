package gr.ntua.vision.monitoring.queues;

import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.APPLICATION_CDMI_QUEUE;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI_VERSION;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;


/**
 * The CDMI Queue interface. This is the controller that stands between the client and the event registry.
 */
@Path("queues")
@Consumes(APPLICATION_CDMI_QUEUE)
@Produces(APPLICATION_CDMI_QUEUE)
public class CDMIQueuesResource {
    /***/
    private String               corsHeaders;
    /** the event registry. */
    private final QueuesRegistry registry;


    /**
     * Constructor.
     * 
     * @param registry
     */
    public CDMIQueuesResource(final QueuesRegistry registry) {
        this.registry = registry;
    }


    /**
     * This OPTIONS request/response is necessary if you consumes other format than text/plain or if you use other HTTP verbs than
     * GET and POST
     * 
     * @param accessControlRequestHeaders
     * @return an 200 response.
     */
    @OPTIONS
    public Response corsResource(@HeaderParam("Access-Control-Request-Headers") final String accessControlRequestHeaders) {
        corsHeaders = accessControlRequestHeaders;
        return toCORS(Response.ok(), accessControlRequestHeaders);
    }


    /**
     * This OPTIONS request/response is necessary if you consumes other format than text/plain or if you use other HTTP verbs than
     * GET and POST
     * 
     * @param accessControlRequestHeaders
     * @param queue
     * @return an 200 response.
     */
    @OPTIONS
    @Path("{queue}")
    public Response corsResource(@HeaderParam("Access-Control-Request-Headers") final String accessControlRequestHeaders,
            @SuppressWarnings("unused") @PathParam("queue") final String queue) {
        corsHeaders = accessControlRequestHeaders;
        return toCORS(Response.ok(), accessControlRequestHeaders);
    }


    /**
     * This OPTIONS request/response is necessary if you consumes other format than text/plain or if you use other HTTP verbs than
     * GET and POST
     * 
     * @param accessControlRequestHeaders
     * @param queue
     * @param topic
     * @return an 200 response.
     */
    @OPTIONS
    @Path("{queue}/{topic}")
    public Response corsResource(@HeaderParam("Access-Control-Request-Headers") final String accessControlRequestHeaders,
            @SuppressWarnings("unused") @PathParam("queue") final String queue,
            @SuppressWarnings("unused") @PathParam("topic") final String topic) {
        corsHeaders = accessControlRequestHeaders;
        return toCORS(Response.ok(), accessControlRequestHeaders);
    }


    /**
     * @param queueName
     * @param topic
     * @return the {@link Response} object.
     */
    @Path("{queue}/{topic}")
    @PUT
    public Response createQueue(@PathParam("queue") final String queueName, @PathParam("topic") final String topic) {
        try {
            final CDMIQueue q = registry.register(queueName, topic);

            return toCORS(Response.created(URI.create("/")).header(X_CDMI, X_CDMI_VERSION).entity(CDMIQueue.toBean(q)));
        } catch (final CDMIQueueException e) {
            return toCORS(Response.status(Status.BAD_REQUEST).header(X_CDMI, X_CDMI_VERSION).type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(e.getMessage()));
        }
    }


    /**
     * @param queueName
     * @param uriInfo
     * @return the {@link Response} object.
     */
    @Path("{queue}")
    @DELETE
    public Response deleteQueue(@PathParam("queue") final String queueName, @Context UriInfo uriInfo) {
        final String query = uriInfo.getRequestUri().getQuery();

        try {
            if (query != null && query.length() > 0) {
                if (!query.contains("value"))
                    throw new CDMIQueueException("query not supported: " + query);

                final int count = Integer.valueOf(query.replace("=", "").replaceFirst("value(s:)?", ""));

                registry.removeEvents(queueName, count);
            } else {
                registry.unregister(queueName);
            }

            return toCORS(Response.noContent().header(X_CDMI, X_CDMI_VERSION));
        } catch (final CDMIQueueException e) {
            return toCORS(Response.status(Status.BAD_REQUEST).header(X_CDMI, X_CDMI_VERSION).type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(e.getMessage()));
        }
    }


    /**
     * @return the list of available topics.
     * @see gr.ntua.vision.monitoring.queues.QueuesRegistry#listAvailableTopics()
     */
    @GET
    @Path("topics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableTopics() {
        return toCORS(Response.ok(registry.listAvailableTopics()));
    }


    /**
     * Node: this is an extension to CDMI.
     * 
     * @return the list of queues.
     * @see gr.ntua.vision.monitoring.queues.QueuesRegistry#list()
     */
    @GET
    public Response listQueues() {
        final ArrayList<CDMIQueueBean> beans = new ArrayList<CDMIQueueBean>();

        for (final CDMIQueue q : registry.list())
            beans.add(CDMIQueue.toBean(q));

        return toCORS(Response.ok(beans));
    }


    /**
     * Get the list of available events in the queue.
     * 
     * @param queueName
     *            the name of the queue.
     * @return the list of events as a json array.
     */
    @Path("{queue}")
    @GET
    public Response readQueue(@PathParam("queue") final String queueName) {
        try {
            return cdmiReadQueueResponse(queueName, registry.getCDMIEvents(queueName));
        } catch (final CDMIQueueException e) {
            return toCORS(Response.status(Status.BAD_REQUEST).header(X_CDMI, X_CDMI_VERSION).type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(e.getMessage()));
        }
    }


    /**
     * @param queueName
     * @param list
     *            the list of cdmi events.
     * @return the cdmi successfully created queue response.
     */
    private Response cdmiReadQueueResponse(final String queueName, final List<Map<String, Object>> list) {
        String topic = null;

        for (final CDMIQueue q : registry.list())
            if (q.name.equals(queueName)) {
                topic = q.topic;
                break;
            }

        if (topic == null)
            throw new CDMIQueueException("no such queue available: " + queueName);

        final CDMIQueueListBean bean = new CDMIQueueListBean(queueName, topic);

        bean.setValue(list);

        return toCORS(Response.ok(bean).header(X_CDMI, X_CDMI_VERSION));
    }


    /**
     * @param req
     * @return CORS-ed a response object.
     */
    private Response toCORS(final ResponseBuilder req) {
        return toCORS(req, corsHeaders);
    }


    /**
     * @param req
     * @param returnMethod
     * @return a cors-ed response object.
     */
    private static Response toCORS(final ResponseBuilder req, final String returnMethod) {
        final ResponseBuilder rb = req.header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods",
                                                                                         "GET, POST, PUT, DELETE, OPTIONS");

        if (returnMethod != null && !returnMethod.isEmpty())
            rb.header("Access-Control-Allow-Headers", returnMethod);

        return rb.build();
    }
}
