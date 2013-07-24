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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


/**
 * The CDMI Queue interface. This is the controller that stands between the client and the event registry.
 */
@Path("queues")
@Consumes(APPLICATION_CDMI_QUEUE)
@Produces(APPLICATION_CDMI_QUEUE)
public class CDMIQueuesResource {
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
     * @param queueName
     * @param topic
     * @return the {@link Response} object.
     */
    @Path("{queue}/{topic}")
    @PUT
    public Response createQueue(@PathParam("queue") final String queueName, @PathParam("topic") final String topic) {
        try {
            final CDMIQueue q = registry.register(queueName, topic);

            return Response.created(URI.create("/")).header(X_CDMI, X_CDMI_VERSION).entity(CDMIQueue.toBean(q)).build();
        } catch (final CDMIQueueException e) {
            return Response.status(Status.BAD_REQUEST).header(X_CDMI, X_CDMI_VERSION).type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(e.getMessage()).build();
        }
    }


    /**
     * @param queueName
     * @return the {@link Response} object.
     */
    @Path("{queue}")
    @DELETE
    public Response deleteQueue(@PathParam("queue") final String queueName) {
        try {
            if (queueName.endsWith("?value") || queueName.contains("?values:")) {
                final String[] fs = queueName.split("\\?");
                final String qName = fs[0];
                final int count = Integer.valueOf(fs[1].replaceFirst("value[s][:]", ""));

                registry.removeEvents(qName, count);
            } else
                registry.unregister(queueName);

            return Response.noContent().header(X_CDMI, X_CDMI_VERSION).build();
        } catch (final CDMIQueueException e) {
            return Response.status(Status.BAD_REQUEST).header(X_CDMI, X_CDMI_VERSION).type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(e.getMessage()).build();
        }
    }


    /**
     * @return the list of available topics.
     * @see gr.ntua.vision.monitoring.queues.QueuesRegistry#listAvailableTopics()
     */
    @GET
    @Path("topics")
    @Produces(MediaType.APPLICATION_JSON)
    public String[] getAvailableTopics() {
        return registry.listAvailableTopics();
    }


    /**
     * Node: this is an extension to CDMI.
     * 
     * @return the list of queues.
     * @see gr.ntua.vision.monitoring.queues.QueuesRegistry#list()
     */
    @GET
    public List<CDMIQueueBean> listQueues() {
        final ArrayList<CDMIQueueBean> beans = new ArrayList<CDMIQueueBean>();

        for (final CDMIQueue q : registry.list())
            beans.add(CDMIQueue.toBean(q));

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
            return cdmiReadQueueResponse(queueName, registry.getCDMIEvents(queueName));
        } catch (final CDMIQueueException e) {
            return Response.status(Status.BAD_REQUEST).header(X_CDMI, X_CDMI_VERSION).type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(e.getMessage()).build();
        }
    }


    /**
     * @param queueName
     * @param list
     *            the list of cdmi events.
     * @return the cdmi successfully created queue response.
     */
    private static Response cdmiReadQueueResponse(final String queueName, final List<Map<String, Object>> list) {
        final CDMIQueueListBean bean = new CDMIQueueListBean(queueName);

        bean.setValue(list);

        return Response.ok(bean).header(X_CDMI, X_CDMI_VERSION).build();
    }
}
