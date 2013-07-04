package gr.ntua.vision.monitoring.queues;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


/**
 *
 */
@Path("queues")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class QueuesResource {
    /***/
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
     * @param queueName
     * @param topic
     * @return
     */
    @Path("{queue}/{topic}")
    @POST
    public Response registerQueue(@PathParam("queue") final String queueName, @PathParam("topic") final String topic) {
        try {
            final TopicedQueue q = registry.register(queueName, topic);

            return Response.created(URI.create("/")).entity(TopicedQueue.toBean(q)).build();
        } catch (final QueuesRegistrationError e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity(e.getMessage()).build();
        }
    }
}
