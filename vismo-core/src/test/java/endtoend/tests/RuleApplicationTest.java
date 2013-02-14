package endtoend.tests;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.events.VismoEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;


/**
 * This is used to test the infrastructure
 */
public class RuleApplicationTest {
    /**
     * This is used to verify that the expected number of events for given operation were received. We only care about operations
     * on objects, not containers.
     */
    private static class NoEventsPerOperationHandler implements EventHandler {
        /***/
        private static final String ORIGINATING_SERVICE = "object_service";
        /** this is used to drop intermediate (vismo-dispatch) events. */
        private static final String SPECIAL_FIELD       = "transaction-throughput";
        /***/
        private int                 noReceivedEvents    = 0;
        /***/
        private final String        operation;


        /**
         * Constructor.
         * 
         * @param operation
         */
        public NoEventsPerOperationHandler(final String operation) {
            this.operation = operation;
        }


        /**
         * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
         */
        @Override
        public void handle(final MonitoringEvent e) {
            if (!isObsEvent(e))
                return;
            if (!isfullObsEvent(e))
                return;
            if (isContainerOperationEvent(e))
                return;

            if (operation.equals(e.get("operation")))
                ++noReceivedEvents;
        }


        /**
         * @param noExpectedEvents
         */
        public void haveReceivedExpectedNoEvents(final int noExpectedEvents) {
            assertEquals(noExpectedEvents, noReceivedEvents);
        }


        /**
         * @param e
         * @return <code>true</code> iff the event represents an operation on a container, <code>false</code> otherwise.
         */
        private static boolean isContainerOperationEvent(final MonitoringEvent e) {
            final String objectName = (String) e.get("object");

            return objectName == null || objectName.isEmpty();
        }


        /**
         * @param e
         * @return <code>true</code> iff the events is an obs event with calculated fields (throughput, latency, etc),
         *         <code>false</code> otherwise.
         */
        private static boolean isfullObsEvent(final MonitoringEvent e) {
            return e.get(SPECIAL_FIELD) != null;
        }


        /**
         * @param e
         * @return <code>true</code> iff this is an event that comes from the obs, <code>false</code> otherwise.
         */
        private static boolean isObsEvent(final MonitoringEvent e) {
            return ORIGINATING_SERVICE.equals(e.originatingService());
        }
    }

    /***/
    private static final int                  CONSUMERS_PORT     = 56430;
    /** the machine's ip */
    private static final String               HOST_URL           = "10.0.1.101";
    /***/
    private static final Logger               log                = LoggerFactory.getLogger(RuleApplicationTest.class);
    /***/
    private static final String               OBJ_NAME           = "my-vismo-test-object-1";
    /***/
    private static final String               PASS               = "123";
    /***/
    private static final String               TENANT             = "ntua";
    /***/
    private static final String               TEST_CONTAINER     = "vismo-test-end-to-end";
    /***/
    private static final String               USER               = "bill";
    /***/
    private final Client                      client             = new Client();
    /***/
    private final NoEventsPerOperationHandler GET_OBJECT_HANDLER = new NoEventsPerOperationHandler("GET");
    /***/
    private final NoEventsPerOperationHandler PUT_OBJECT_HANDLER = new NoEventsPerOperationHandler("PUT");
    /***/
    private final VismoEventRegistry          registry           = new VismoEventRegistry(new ZMQFactory(new ZContext()),
                                                                         "tcp://" + HOST_URL + ":" + CONSUMERS_PORT);


    /**
     * This is a sanity check for the infrastructure.
     * 
     * @throws InterruptedException
     */
    @Test
    public void producersShouldReceiveDefaultObsEvents() throws InterruptedException {
        putObject(TEST_CONTAINER, OBJ_NAME, "{ \"foo\": \"bar\", \"is-test\": \"true\", \"value\": \"hello-world\" }");
        Thread.sleep(2000);
        shouldHaveReceivedEvent(PUT_OBJECT_HANDLER, 1);
        readObject(TEST_CONTAINER, OBJ_NAME);
        Thread.sleep(2000);
        shouldHaveReceivedEvent(GET_OBJECT_HANDLER, 1);
    }


    /***/
    @Before
    public void setUp() {
        setupHTTPClient();
        setupConsumers();
        createContainer(TEST_CONTAINER);
    }


    /***/
    @After
    public void tearDown() {
        // deleteObject(TEST_CONTAINER, OBJ_NAME);
        deleteContainer(TEST_CONTAINER);
    }


    /**
     * @return a resource pointing to the entry point of <code>Containers</code>.
     */
    private WebResource containers() {
        return client.resource("http://" + HOST_URL + ":8080").path("containers");
    }


    /**
     * @param cont
     */
    private void createContainer(final String cont) {
        log.debug("creating container {}", cont);

        final ClientResponse res = containers().path(TENANT).path(cont).put(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
    }


    /**
     * @param cont
     */
    private void deleteContainer(final String cont) {
        log.debug("deleting container {}", cont);

        final ClientResponse res = containers().path(TENANT).path(cont).delete(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
    }


    /**
     * @param container
     * @param object
     */
    private void deleteObject(final String container, final String object) {
        log.debug("deleting object {} under {}", object, container);

        final ClientResponse res = forObject(container, object).delete(ClientResponse.class);

        assertEquals(ClientResponse.Status.NO_CONTENT, res.getClientResponseStatus());
    }


    /**
     * @param container
     * @param object
     * @return a {@link Builder}.
     */
    private Builder forObject(final String container, final String object) {
        return obs().path(TENANT).path(container).path(object).type("application/cdmi-object").accept("application/cdmi-object")
                .header("X-CDMI-Specification-Version", "1.0");

    }


    /**
     * @return a resource pointing to the entry point of <code>Object Service</code>.
     */
    private WebResource obs() {
        return client.resource("http://" + HOST_URL).path("vision-cloud").path("object-service");
    }


    /**
     * @param container
     * @param object
     * @param payload
     */
    private void putObject(final String container, final String object, final String payload) {
        log.debug("creating object {} under {}", object, container);

        final ClientResponse res = forObject(container, object).entity(payload).put(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
    }


    /**
     * @param container
     * @param object
     */
    private void readObject(final String container, final String object) {
        log.debug("reading back object {} under {}", object, container);

        final ClientResponse res = forObject(container, object).get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
    }


    /***/
    private void setupConsumers() {
        registry.registerToAll(PUT_OBJECT_HANDLER);
        registry.registerToAll(GET_OBJECT_HANDLER);
        registry.registerToAll(new EventHandler() {
            @Override
            public void handle(final MonitoringEvent me) {
                final VismoEvent e = (VismoEvent) me;

                System.err.println("receiving: " + e.dict());
            }
        });
    }


    /***/
    private void setupHTTPClient() {
        client.addFilter(new HTTPBasicAuthFilter(USER + "@" + TENANT, PASS));
        client.addFilter(new LoggingFilter(System.err));
    }


    /**
     * Check that the handler has received the given number of events.
     * 
     * @param handler
     * @param noExpectedEvents
     */
    private static void shouldHaveReceivedEvent(final NoEventsPerOperationHandler handler, final int noExpectedEvents) {
        handler.haveReceivedExpectedNoEvents(noExpectedEvents);
    }
}
