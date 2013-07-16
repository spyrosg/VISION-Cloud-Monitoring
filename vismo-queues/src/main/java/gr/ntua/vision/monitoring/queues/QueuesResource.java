package gr.ntua.vision.monitoring.queues;

import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.APPLICATION_CDMI_QUEUE;
// import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI;
//import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI_VERSION;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI_VERSION;
import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import unit.tests.InMemoryEventRegistry.MyEvent;


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
     * @param queueName
     * @param topic
     * @return the {@link Response} object.
     */
    @Path("{queue}/{topic}")
    @PUT
    public Response createQueue(@PathParam("queue") final String queueName, @PathParam("topic") final String topic) {
        try {
            final TopicedQueue q = registry.register(queueName, topic);

            return cdmiCreateQueueResponse(TopicedQueue.toBean(q));
        } catch (final QueuesRegistrationException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity(e.getMessage()).build();
        }
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
            return cdmiReadQueueResponse(queueName, registry.getEvents(queueName));
        } catch (final NoSuchQueueException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN_TYPE).entity(e.getMessage()).build();
        }
    }


    /**
     * @param bean
     * @return the cdmi successfully created queue response.
     */
    private static Response cdmiCreateQueueResponse(final TopicedQueueBean bean) {
        return Response.created(URI.create("/")).header(X_CDMI, X_CDMI_VERSION).entity(bean).build();
    }


    /**
     * @param queueName
     * @param list
     * @return the cdmi successfully created queue response.
     */
    private static Response cdmiReadQueueResponse(final String queueName, final List<MonitoringEvent> list) {
        final TopicedQueueListBean bean = new TopicedQueueListBean(queueName);
        final ArrayList<Map<String, Object>> values = new ArrayList<Map<String, Object>>(list.size());

        for (final MonitoringEvent e : list)
            values.add(((MyEvent) e).dict);

        bean.setQueueValues("1-" + list.size());
        bean.setValue(values);

        return Response.ok(bean).header(X_CDMI, X_CDMI_VERSION).build();
    }
}
