package endtoend.tests;

import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.APPLICATION_CDMI_QUEUE_TYPE;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI;
import static gr.ntua.vision.monitoring.queues.CDMIQueueMediaTypes.X_CDMI_VERSION;
import static java.lang.Thread.sleep;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.queues.CDMIQueueListBean;
import gr.ntua.vision.monitoring.queues.CDMIQueueProdiver;
import gr.ntua.vision.monitoring.queues.CDMIQueuesResource;
import gr.ntua.vision.monitoring.queues.QueuesRegistry;
import gr.ntua.vision.monitoring.web.WebAppBuilder;
import integration.tests.JerseyResourceTest;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;


/**
 *
 */
public class ReceivingEventsOffCDMIQueue extends JerseyResourceTest {
    // TODO: should also test storlets
    /**
     *
     */
    private static class ObsEventHandler implements EventHandler {
        /***/
        private static final String             OBS     = "object_service";
        /***/
        private static final String             SUCCESS = "SUCCESS";
        /***/
        public final ArrayList<MonitoringEvent> events;
        /***/
        private final String                    operation;


        /**
         * Constructor.
         * 
         * @param operation
         */
        public ObsEventHandler(final String operation) {
            this.operation = operation;
            this.events = new ArrayList<MonitoringEvent>();
        }


        /**
         * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
         */
        @Override
        public void handle(final MonitoringEvent e) {
            log.debug("received: {}", e.serialize());

            if (e.topic() != null)
                return;
            if (!operation.equals(e.get("operation")))
                return;
            if (!OBS.equals(e.originatingService()))
                return;
            if (!SUCCESS.equals(e.get("status")))
                return;

            log.debug("match! {}", e.serialize());
            events.add(e);
        }


        /**
         * @param expected
         */
        public void shouldHaveReceivedExpectedNoEvents(final int expected) {
            assertEquals(expected, events.size());
        }
    }

    /***/
    static final Logger            log            = LoggerFactory.getLogger(ReceivingEventsOffCDMIQueue.class);
    /***/
    private static final String    HOST_URL       = "10.0.1.101";
    /***/
    private static final String    PASS           = "1234";
    /***/
    private static final int       PORT           = 56430;
    /***/
    private static final String    QUEUE          = "test-queue";
    /***/
    private static final String    TENANT         = "ntua";
    /***/
    private static final String    TEST_CONTAINER = "foo5";
    /***/
    private static final String    TOPIC          = "reads";
    /***/
    private static final String    USER           = "vassilis";
    /***/
    private final VisionHTTPClient client         = new VisionHTTPClient(HOST_URL, TENANT, USER, PASS);
    /***/
    private ObsEventHandler        getHandler;
    /***/
    private VismoEventRegistry     registry;


    /**
     * @see integration.tests.JerseyResourceTest#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        registry = new VismoEventRegistry("tcp://" + HOST_URL + ":" + PORT);
        registry.registerToAll(getHandler = new ObsEventHandler("GET"));

        final QueuesRegistry queuesRegistry = new QueuesRegistry(registry);
        final WebAppBuilder builder = new WebAppBuilder();

        builder.addProvider(CDMIQueueProdiver.class);
        builder.addResource(new CDMIQueuesResource(queuesRegistry));

        configureServer(builder.build(), "/*");
        startServer();
    }


    /**
     * @see integration.tests.JerseyResourceTest#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        if (registry != null)
            registry.halt();

        super.tearDown();
    }


    /**
     * @throws Exception
     */
    public void testShouldReceiveEvents() throws Exception {
        createCDMIQueue(QUEUE, TOPIC);
        readObject("foo");
        sleep(3000);
        getHandler.shouldHaveReceivedExpectedNoEvents(1);

        final CDMIQueueListBean list = readCDMIQueue(QUEUE);

        assertEquals("0-1", list.getQueueValues());
        assertEquals(TOPIC, list.getTopic());
        assertEquals(getHandler.events.get(0), list.getValue());
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
     */
    private void createCDMIQueue(final String queueName, final String topic) {
        final ClientResponse res = resource().path(queueName).path(topic).accept(APPLICATION_CDMI_QUEUE_TYPE)
                .type(APPLICATION_CDMI_QUEUE_TYPE).header(X_CDMI, X_CDMI_VERSION).put(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
    }


    /**
     * @param objName
     */
    private void putObject(final String objName) {
        client.putObject(TENANT, TEST_CONTAINER, objName,
                         "{ \"spam\": \"ham\", \"is-test\": \"true\", \"value\": \"hello-world\" }");
    }


    /**
     * Read a queue, with a CDMI compliant call.
     * 
     * @param queueName
     * @return the {@link CDMIQueueListBean} object.
     */
    private CDMIQueueListBean readCDMIQueue(final String queueName) {
        final ClientResponse res = resource().path(queueName).accept(APPLICATION_CDMI_QUEUE_TYPE).header(X_CDMI, X_CDMI_VERSION)
                .get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());

        return res.getEntity(CDMIQueueListBean.class);
    }


    /**
     * @param objName
     */
    private void readObject(final String objName) {
        client.getObject(TENANT, TEST_CONTAINER, objName);
    }
}
