package endtoend.tests;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.events.VismoEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
    /***/
    private static final int                     CONSUMERS_PORT     = 56430;
    /***/
    private static final NoEventsCheckingHandler FOO_RULE_HANDLER   = null;
    /** the machine's ip */
    private static final String                  HOST_URL           = "10.0.1.101";
    /***/
    private static final Logger                  log                = LoggerFactory.getLogger(RuleApplicationTest.class);
    /***/
    private static final String                  OBJ_NAME           = "my-vismo-test-object-1";
    /***/
    private static final String                  PASS               = "123";
    /***/
    private static final String                  TENANT             = "ntua";
    /***/
    private static final String                  TEST_CONTAINER     = "vismo-test-end-to-end";
    /***/
    private static final String                  USER               = "bill";
    /***/
    private final Client                         client             = new Client();
    /***/
    private final PerOperationHandler            GET_OBJECT_HANDLER = new PerOperationHandler("GET");
    /***/
    private final PerOperationHandler            PUT_OBJECT_HANDLER = new PerOperationHandler("PUT");
    /***/
    private final VismoEventRegistry             registry           = new VismoEventRegistry(new ZMQFactory(new ZContext()),
                                                                            "tcp://" + HOST_URL + ":" + CONSUMERS_PORT);


    /**
     * @throws InterruptedException
     */
    @Ignore
    @Test
    public void foo() throws InterruptedException {
        registry.registerToAll(FOO_RULE_HANDLER);
        submitRule("FooRule");

        putObject(TEST_CONTAINER, OBJ_NAME, "{ \"foo\": \"bar\", \"is-test\": \"true\", \"value\": \"hello-world\" }");
        Thread.sleep(2000);
        shouldHaveReceivedEvent(FOO_RULE_HANDLER, 1);
    }


    /**
     * This is a sanity check for the infrastructure.
     * 
     * @throws InterruptedException
     */
    @Test
    public void producersShouldReceiveDefaultObsEvents() throws InterruptedException {
        registry.registerToAll(PUT_OBJECT_HANDLER);
        registry.registerToAll(GET_OBJECT_HANDLER);

        putObject(TEST_CONTAINER, OBJ_NAME, "{ \"foo\": \"bar\", \"is-test\": \"true\", \"value\": \"hello-world\" }");
        waitForEventsToBeReceived();
        shouldHaveReceivedEvent(PUT_OBJECT_HANDLER, 1);

        readObject(TEST_CONTAINER, OBJ_NAME);
        waitForEventsToBeReceived();
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
    WebResource containers() {
        return client.resource("http://" + HOST_URL + ":8080").path("containers");
    }


    /**
     * @param cont
     */
    private void createContainer(final String cont) {
        final long dur = new TimedCodeBlock() {
            @Override
            public void withBlock() {
                final ClientResponse res = containers().path(TENANT).path(cont).put(ClientResponse.class);

                assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
            }
        }.run().getDuration();

        log.debug("creating container {} in {} ms", cont, dur);
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
     * @param string
     */
    private void submitRule(final String string) {
        // TODO Auto-generated method stub

    }


    /**
     * Check that the handler has received the given number of events.
     * 
     * @param handler
     * @param noExpectedEvents
     */
    private static void shouldHaveReceivedEvent(final NoEventsCheckingHandler handler, final int noExpectedEvents) {
        handler.haveReceivedExpectedNoEvents(noExpectedEvents);
    }


    /**
     * @throws InterruptedException
     */
    private static void waitForEventsToBeReceived() throws InterruptedException {
        Thread.sleep(1000);
    }
}
