package endtoend.tests;

import gr.ntua.vision.monitoring.events.MapBasedEvent;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.zeromq.ZContext;


/**
 * This is used to test the main rule update functionality. See {@link #verifyRuleApplicationWithEventsConsumption()}.
 */
@Ignore("requires testbed vpn connectivity")
public class RuleApplicationTest {
    /***/
    private static final int                     CONSUMERS_PORT     = 56430;
    /***/
    private static final NoEventsCheckingHandler FOO_RULE_HANDLER   = null;
    /** the machine's ip */
    private static final String                  HOST_URL           = "10.0.1.101";
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
    private final VisionHTTPClient               client             = new VisionHTTPClient(HOST_URL, TENANT, USER, PASS);
    /***/
    private final PerOperationHandler            GET_OBJECT_HANDLER = new PerOperationHandler("GET");
    /***/
    private final PerOperationHandler            PUT_OBJECT_HANDLER = new PerOperationHandler("PUT");
    /***/
    private final VismoEventRegistry             registry           = new VismoEventRegistry(new ZMQFactory(new ZContext()),
                                                                            "tcp://" + HOST_URL + ":" + CONSUMERS_PORT);


    /**
     * This is a sanity check for the infrastructure.
     * 
     * @throws InterruptedException
     */
    @Test
    public void producersShouldReceiveDefaultObsEvents() throws InterruptedException {
        registry.registerToAll(PUT_OBJECT_HANDLER);
        registry.registerToAll(GET_OBJECT_HANDLER);

        client.putObject(TEST_CONTAINER, OBJ_NAME, "{ \"foo\": \"bar\", \"is-test\": \"true\", \"value\": \"hello-world\" }");
        waitForEventsToBeReceived();
        shouldHaveReceivedEvent(PUT_OBJECT_HANDLER, 1);

        client.getObject(TEST_CONTAINER, OBJ_NAME);
        waitForEventsToBeReceived();
        shouldHaveReceivedEvent(GET_OBJECT_HANDLER, 1);
    }


    /***/
    @Before
    public void setUp() {
        setupConsumers();
        client.createContainer(TEST_CONTAINER);
    }


    /***/
    @After
    public void tearDown() {
        client.deleteContainer(TEST_CONTAINER);
    }


    /**
     * Submit and apply a rule, collect the events from given rule. The plan for the test:
     * <ol>
     * <li>Register a new handler that corresponds to events from a rule.
     * <li>
     * <li>Submit a rule to a running vismo instance.</li>
     * <li>Perform some operations on the OBS.</li>
     * <li>Collect and verify the events triggered by the rule added.</li>.
     * </ol>
     * 
     * @throws InterruptedException
     */
    @Ignore("WIP")
    @Test
    public void verifyRuleApplicationWithEventsConsumption() throws InterruptedException {
        registry.registerToAll(FOO_RULE_HANDLER);
        // TODO submitRule("FooRule");

        client.putObject(TEST_CONTAINER, OBJ_NAME, "{ \"foo\": \"bar\", \"is-test\": \"true\", \"value\": \"hello-world\" }");
        Thread.sleep(2000);
        shouldHaveReceivedEvent(FOO_RULE_HANDLER, 1);
    }


    /***/
    private void setupConsumers() {
        registry.registerToAll(new EventHandler() {
            @Override
            public void handle(final MonitoringEvent me) {
                final MapBasedEvent e = (MapBasedEvent) me;

                System.err.println("receiving: " + e.dict());
            }
        });
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
