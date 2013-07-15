package gr.ntua.vision.monitoring.queues;

import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.APPLICATION_CDMI_QUEUE;
// import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI;
//import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI_VERSION;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI_VERSION;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


/**
 * CDMI Notification Queues interface.
 */
@Path("queues")
@Consumes(APPLICATION_CDMI_QUEUE)
@Produces(APPLICATION_CDMI_QUEUE)
public class QueuesResource {
    /** the event registry. */
    private final QueuesRegistry registry;


    /**
     * Constructor.
     * 
     * @param registry
     */
    public QueuesResource(final QueuesRegistry registry) {
        this.registry = registry;
    }


    /**
     * @return the list of available topics.
     * @see gr.ntua.vision.monitoring.queues.QueuesRegistry#listAvailableTopics()
     */
    @GET
    @Path("topics")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getAvailableTopics() {
        return registry.listAvailableTopics();
    }


    /**
     * @return the list of queues.
     * @see gr.ntua.vision.monitoring.queues.QueuesRegistry#list()
     */
    @GET
    public List<TopicedQueueBean> listQueues() {
        final ArrayList<TopicedQueueBean> beans = new ArrayList<TopicedQueueBean>();

        for (final TopicedQueue q : registry.list())
            beans.add(TopicedQueue.toBean(q));

        return beans;
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
            final String entity = registry.eventsToJSONString(queueName);

            return Response.ok(entity).build();
        } catch (final NoSuchQueueException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity(e.getMessage()).build();
        }
    }


    /**
     * @param queueName
     * @param topic
     * @return the {@link Response} object.
     */
    @Path("{queue}/{topic}")
    @PUT
    public Response registerQueue(@PathParam("queue") final String queueName, @PathParam("topic") final String topic) {
        try {
            final TopicedQueue q = registry.register(queueName, topic);

            return cdmiResponse(TopicedQueue.toBean(q));
        } catch (final QueuesRegistrationException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity(e.getMessage()).build();
        }
    }


    /**
     * @param bean 
     * @return
     */
    private static Response cdmiResponse(final TopicedQueueBean bean) {
        return Response.created(URI.create("/")).header(X_CDMI, X_CDMI_VERSION).entity(bean).build();
    }
}
