package endtoend.tests;

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


/**
 * This is used to test the infrastructure
 */
public class RuleApplicationTest extends HTTPClientTest {
    /** the machine's ip */
    static final String                          HOST_URL           = "10.0.1.101";
    /***/
    static final Logger                          log                = LoggerFactory.getLogger(RuleApplicationTest.class);
    /***/
    static final String                          TENANT             = "ntua";
    /***/
    private static final int                     CONSUMERS_PORT     = 56430;
    /***/
    private static final NoEventsCheckingHandler FOO_RULE_HANDLER   = null;
    /***/
    private static final String                  OBJ_NAME           = "my-vismo-test-object-1";
    /***/
    private static final String                  PASS               = "123";
    /***/
    private static final String                  TEST_CONTAINER     = "vismo-test-end-to-end";
    /***/
    private static final String                  USER               = "bill";
    /***/
    private final PerOperationHandler            GET_OBJECT_HANDLER = new PerOperationHandler("GET");
    /***/
    private final PerOperationHandler            PUT_OBJECT_HANDLER = new PerOperationHandler("PUT");
    /***/
    private final VismoEventRegistry             registry           = new VismoEventRegistry(new ZMQFactory(new ZContext()),
                                                                            "tcp://" + HOST_URL + ":" + CONSUMERS_PORT);


    /**
     * Constructor.
     */
    public RuleApplicationTest() {
        super(HOST_URL, TENANT, USER, PASS);
    }


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

        getObject(TEST_CONTAINER, OBJ_NAME);
        waitForEventsToBeReceived();
        shouldHaveReceivedEvent(GET_OBJECT_HANDLER, 1);
    }


    /***/
    @Before
    public void setUp() {
        setupConsumers();
        createContainer(TEST_CONTAINER);
    }


    /***/
    @After
    public void tearDown() {
        // deleteObject(TEST_CONTAINER, OBJ_NAME);
        deleteContainer(TEST_CONTAINER);
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
