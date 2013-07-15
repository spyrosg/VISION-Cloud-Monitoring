package integration.tests;

import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.APPLICATION_CDMI_QUEUE;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.APPLICATION_CDMI_QUEUE_TYPE;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI_VERSION;
import gr.ntua.vision.monitoring.queues.CDMIQueueProdiver;
import gr.ntua.vision.monitoring.queues.QueuesRegistry;
import gr.ntua.vision.monitoring.queues.QueuesResource;
import gr.ntua.vision.monitoring.queues.TopicedQueueBean;
import gr.ntua.vision.monitoring.queues.TopicedQueueListBean;
import gr.ntua.vision.monitoring.web.WebAppBuilder;

import java.util.List;

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
public class QueuesResourceTest extends JerseyResourceTest {
    /***/
    private static final String   MY_QUEUE = "my-queue";
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

        eventGenerator = new InMemoryEventRegistry(MY_QUEUE);
        registry = new QueuesRegistry(eventGenerator);

        final WebAppBuilder builder = new WebAppBuilder();

        builder.addProvider(CDMIQueueProdiver.class);
        builder.addResource(new QueuesResource(registry));

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
        assertResponseIsCDMICompliant(QUEUE_NAME, res.getEntity(TopicedQueueBean.class));
    }


    /**
     * @throws Exception
     */
    public void testShouldListAvailableTopics() throws Exception {
        final ClientResponse res = resource().path("topics").accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());

        @SuppressWarnings("unchecked")
        final List<String> topics = res.getEntity(List.class);

        assertEquals(5, topics.size());
    }


    /**
     * @throws Exception
     */
    public void testShouldListUserQueues() throws Exception {
        final ClientResponse res = createCDMIQueue(MY_QUEUE, "reads");

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());

        final ClientResponse res1 = resource().accept(APPLICATION_CDMI_QUEUE_TYPE).type(APPLICATION_CDMI_QUEUE_TYPE)
                .header(X_CDMI, X_CDMI_VERSION).get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res1.getClientResponseStatus());

        @SuppressWarnings("unchecked")
        final List<TopicedQueueBean> queues = res1.getEntity(List.class);

        assertEquals(1, queues.size());
    }


    /**
     * @throws Exception
     */
    public void testShouldReadEventsOffTheQueue() throws Exception {
        final ClientResponse res = createCDMIQueue(MY_QUEUE, "reads");

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        eventGenerator.pushEvents(10);

        final ClientResponse res1 = readCDMIQueue(MY_QUEUE);

        assertEquals(ClientResponse.Status.OK, res1.getClientResponseStatus());
        final MultivaluedMap<String, String> headers = res1.getHeaders();

        assertEquals(APPLICATION_CDMI_QUEUE, headers.getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals(X_CDMI_VERSION, headers.getFirst(X_CDMI));

        assertResponseIsCDMICompliant(MY_QUEUE, res1.getEntity(TopicedQueueListBean.class));
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
     */
    private static void assertResponseIsCDMICompliant(final String queueName, final TopicedQueueBean bean) {
        assertEquals(APPLICATION_CDMI_QUEUE, bean.getObjectType());
        assertNotNull(bean.getObjectID());
        assertEquals(queueName, bean.getObjectName());
        assertEquals("/", bean.getParentURI());
        assertNotNull(bean.getParentID());
        assertEquals("/cdmi_domains/" + queueName + "/", bean.getDomainURI());
        assertEquals("/cdmi_capabilities/queue/", bean.getCapabilitiesURI());
        assertEquals("Complete", bean.getCompletionStatus());
        assertEquals(0, bean.getMetadata().size());
        assertEquals("", bean.getQueueValues());
    }


    /**
     * @param queueName
     * @param bean
     */
    private static void assertResponseIsCDMICompliant(final String queueName, final TopicedQueueListBean bean) {
        assertResponseIsCDMICompliant(queueName, (TopicedQueueBean) bean);
        assertEquals(0, bean.getValue().size());
    }
}
