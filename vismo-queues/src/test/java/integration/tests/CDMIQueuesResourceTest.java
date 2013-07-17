package integration.tests;

import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.APPLICATION_CDMI_QUEUE;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.APPLICATION_CDMI_QUEUE_TYPE;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI_VERSION;
import gr.ntua.vision.monitoring.queues.CDMIQueueBean;
import gr.ntua.vision.monitoring.queues.CDMIQueueListBean;
import gr.ntua.vision.monitoring.queues.CDMIQueueProdiver;
import gr.ntua.vision.monitoring.queues.CDMIQueuesResource;
import gr.ntua.vision.monitoring.queues.QueuesRegistry;
import gr.ntua.vision.monitoring.web.WebAppBuilder;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import unit.tests.InMemoryEventRegistry;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;


/**
 * 
 */
public class CDMIQueuesResourceTest extends JerseyResourceTest {
    /***/
    private static final String   MY_QUEUE = "my-queue";
    /***/
    private static final String   MY_TOPIC = "reads";
    /***/
    private InMemoryEventRegistry eventGenerator;
    /***/
    private QueuesRegistry        registry;


    /**
     * @see integration.tests.JerseyResourceTest#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        eventGenerator = new InMemoryEventRegistry(MY_TOPIC);
        registry = new QueuesRegistry(eventGenerator);

        final WebAppBuilder builder = new WebAppBuilder();

        builder.addProvider(CDMIQueueProdiver.class);
        builder.addResource(new CDMIQueuesResource(registry));

        configureServer(builder.build(), "/*");
        startServer();
    }


    /**
     * @throws Exception
     */
    public void testShouldCreateCDMIQueue() throws Exception {
        final String QUEUE_NAME = "q1";
        final ClientResponse res = createCDMIQueue(QUEUE_NAME, "*");

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());

        final MultivaluedMap<String, String> headers = res.getHeaders();

        assertEquals(APPLICATION_CDMI_QUEUE, headers.getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals(X_CDMI_VERSION, headers.getFirst(X_CDMI));
        assertIsCDMICompliantResponse(QUEUE_NAME, res.getEntity(CDMIQueueBean.class), 0);
    }


    /**
     * @throws Exception
     */
    public void testShouldDeleteCDMIQueue() throws Exception {
        final String QUEUE_NAME = "q1";

        assertEquals(ClientResponse.Status.CREATED, createCDMIQueue(QUEUE_NAME, "*").getClientResponseStatus());

        final ClientResponse res = deleteCDMIQueue(QUEUE_NAME);
        final MultivaluedMap<String, String> headers = res.getHeaders();

        assertEquals(ClientResponse.Status.NO_CONTENT, res.getClientResponseStatus());
        assertEquals(X_CDMI_VERSION, headers.getFirst(X_CDMI));
    }


    /**
     * @throws Exception
     */
    public void testShouldListAvailableTopics() throws Exception {
        final ClientResponse res = resource().path("topics").accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());

        @SuppressWarnings("unchecked")
        final List<String> topics = res.getEntity(List.class);

        assertEquals(4, topics.size());
    }


    /**
     * @throws Exception
     */
    public void testShouldListUserQueues() throws Exception {
        assertEquals(ClientResponse.Status.CREATED, createCDMIQueue(MY_QUEUE, "writes").getClientResponseStatus());

        final ClientResponse res = resource().accept(APPLICATION_CDMI_QUEUE_TYPE).type(APPLICATION_CDMI_QUEUE_TYPE)
                .header(X_CDMI, X_CDMI_VERSION).get(ClientResponse.class);
        @SuppressWarnings("unchecked")
        final List<CDMIQueueBean> queues = res.getEntity(List.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        assertEquals(1, queues.size());
    }


    /**
     * @throws Exception
     */
    public void testShouldReadCDMINotificationsOffTheQueue() throws Exception {
        final int NO_EVENTS = 10;

        assertEquals(ClientResponse.Status.CREATED, createCDMIQueue(MY_QUEUE, MY_TOPIC).getClientResponseStatus());
        eventGenerator.pushEvents(NO_EVENTS);

        final ClientResponse res = readCDMIQueue(MY_QUEUE);
        final MultivaluedMap<String, String> headers = res.getHeaders();

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        assertEquals(APPLICATION_CDMI_QUEUE, headers.getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals(X_CDMI_VERSION, headers.getFirst(X_CDMI));
        assertIsCDMICompliantResponse(MY_QUEUE, MY_TOPIC, res.getEntity(CDMIQueueListBean.class), NO_EVENTS);
    }


    /**
     * @throws Exception
     */
    public void testShouldRejectInvalidTopicRequests() throws Exception {
        final String TOPIC = "my-topic";
        final ClientResponse res = createCDMIQueue(MY_QUEUE, TOPIC);

        assertEquals(ClientResponse.Status.BAD_REQUEST, res.getClientResponseStatus());
    }


    /**
     * @see integration.tests.JerseyResourceTest#configureClient(com.sun.jersey.api.client.config.ClientConfig)
     */
    @Override
    protected void configureClient(final ClientConfig cc) {
        super.configureClient(cc);
        cc.getClasses().add(CDMIQueueProdiver.class);
    }


    /**
     * @see integration.tests.JerseyResourceTest#resource()
     */
    @Override
    protected WebResource resource() {
        return root().path("queues");
    }


    /**
     * Create a queue, with a CDMI compliant call.
     * 
     * @param queueName
     * @param topic
     * @return the client's response.
     */
    private ClientResponse createCDMIQueue(final String queueName, final String topic) {
        return resource().path(queueName).path(topic).accept(APPLICATION_CDMI_QUEUE_TYPE).type(APPLICATION_CDMI_QUEUE_TYPE)
                .header(X_CDMI, X_CDMI_VERSION).put(ClientResponse.class);
    }


    /**
     * Delete a queue, with a CDMI compliant call.
     * 
     * @param queueName
     * @return the {@link ClientResponse} object.
     */
    private ClientResponse deleteCDMIQueue(final String queueName) {
        return resource().path(queueName).header(X_CDMI, X_CDMI_VERSION).delete(ClientResponse.class);
    }


    /**
     * Read a queue, with a CDMI compliant call.
     * 
     * @param queueName
     * @return the client's response. It should contain the list of available notifications (events) stored in the queue.
     */
    private ClientResponse readCDMIQueue(final String queueName) {
        return resource().path(queueName).accept(APPLICATION_CDMI_QUEUE_TYPE).header(X_CDMI, X_CDMI_VERSION)
                .get(ClientResponse.class);
    }


    /**
     * @param queueName
     * @param bean
     * @param noExpectedEvents
     */
    private static void assertIsCDMICompliantResponse(final String queueName, final CDMIQueueBean bean, final int noExpectedEvents) {
        assertEquals(APPLICATION_CDMI_QUEUE, bean.getObjectType());
        assertNotNull(bean.getObjectID());
        assertEquals(queueName, bean.getObjectName());
        assertEquals("/", bean.getParentURI());
        assertNotNull(bean.getParentID());
        assertEquals("/cdmi_domains/" + queueName + "/", bean.getDomainURI());
        assertEquals("/cdmi_capabilities/queue/", bean.getCapabilitiesURI());
        assertEquals("Complete", bean.getCompletionStatus());
        assertEquals(0, bean.getMetadata().size());

        if (noExpectedEvents > 0)
            assertEquals("1-" + noExpectedEvents, bean.getQueueValues());
        else
            assertEquals("", bean.getQueueValues());
    }


    /**
     * @param queueName
     * @param topic
     * @param bean
     * @param noExpectedEvents
     */
    private static void assertIsCDMICompliantResponse(final String queueName, final String topic, final CDMIQueueListBean bean,
            final int noExpectedEvents) {
        assertIsCDMICompliantResponse(queueName, bean, noExpectedEvents);
        assertEquals(noExpectedEvents, bean.getValue().size());

        for (final Map<String, Object> event : bean.getValue()) {
            assertEquals(topic, event.get("topic"));
            assertNotNull(event.get("originating-service"));
        }
    }
}
