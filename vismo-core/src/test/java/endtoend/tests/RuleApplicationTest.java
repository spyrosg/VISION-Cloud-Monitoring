package endtoend.tests;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * This is used to test the main rule update functionality. See {@link #verifyRuleApplicationWithEventsConsumption()}.
 */
public class RuleApplicationTest {
    /***/
    private static final int                     CONSUMERS_PORT     = 56430;
    /***/
    private static final NoEventsCheckingHandler FOO_RULE_HANDLER   = null;
    /** the machine's ip */
    private static final String                  HOST_URL           = "10.0.1.101";
    /***/
    private static final String                  OBJ_NAME           = "my-vismo-test-object";
    /***/
    private static final String                  PASS               = "123";
    /***/
    private static final String                  TENANT             = "ntua";
    /***/
    private static final String                  TEST_CONTAINER     = "test-1";
    /***/
    private static final String                  USER               = "bill";
    /***/
    private final VisionHTTPClient               client             = new VisionHTTPClient(HOST_URL, TENANT, USER, PASS);
    /***/
    private final PerOperationHandler            GET_OBJECT_HANDLER = new PerOperationHandler("GET");
    /***/
    private final PerOperationHandler            PUT_OBJECT_HANDLER = new PerOperationHandler("PUT");
    /***/
    private final VismoEventRegistry             registry           = new VismoEventRegistry("tcp://" + HOST_URL + ":"
                                                                            + CONSUMERS_PORT);


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
        waitEventsToBeReceived();
        shouldHaveReceivedEvent(PUT_OBJECT_HANDLER, 1);

        client.getObject(TEST_CONTAINER, OBJ_NAME);
        waitEventsToBeReceived();
        shouldHaveReceivedEvent(GET_OBJECT_HANDLER, 1);
    }


    /***/
    @Before
    public void setUp() {
        setupConsumers();
        // client.createContainer(TEST_CONTAINER);
    }


    /***/
    @After
    public void tearDown() {
        // client.deleteContainer(TEST_CONTAINER);
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
    @Test
    public void verifyRuleApplicationWithEventsConsumption() throws InterruptedException {
        registry.registerToAll(FOO_RULE_HANDLER);
        submitRule(throughputThresholdRule(5, "my-topic", TENANT, USER));

        client.putObject(TEST_CONTAINER, OBJ_NAME, "{ \"foo\": \"bar\", \"is-test\": \"true\", \"value\": \"hello-world\" }");
        waitEventsToBeReceived();
        shouldHaveReceivedEvent(FOO_RULE_HANDLER, 1);
    }


    /***/
    private void setupConsumers() {
        registry.registerToAll(new EventHandler() {
            @Override
            public void handle(final MonitoringEvent me) {
                System.err.println("receiving: " + me.serialize());
            }
        });
    }


    /**
     * @param bean
     */
    private void submitRule(final ThresholdRuleBean bean) {
        client.sumbitRule(bean);
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
     * @param threshold
     * @param topic
     * @param tenant
     * @param user
     * @return a {@link ThresholdRuleBean}.
     */
    private static ThresholdRuleBean throughputThresholdRule(final double threshold, final String topic, final String tenant,
            final String user) {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        // we're concerned about the upload throughout
        bean.setMetric("transaction-throughput");
        bean.setOperation("PUT");
        // under given container
        bean.setFilterUnit(tenant + "," + user);
        // if it's higher...
        bean.setPredicate(">=");
        // than THRESHOLD bytes / second
        bean.setThreshold(threshold);
        // generate event with given topic.
        bean.setTopic(topic);

        return bean;
    }


    /**
     * @throws InterruptedException
     */
    private static void waitEventsToBeReceived() throws InterruptedException {
        Thread.sleep(3000);
    }
}
