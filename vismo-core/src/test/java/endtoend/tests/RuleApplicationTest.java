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
    private static final int                     CONSUMERS_PORT       = 56430;
    /***/
    private static final NoEventsCheckingHandler DEFAULT_RULE_HANDLER = null;
    /** the machine's ip */
    private static final String                  HOST_URL             = "10.0.1.103";
    /***/
    private static final String                  OBJ_NAME             = "my-vismo-test-object";
    /***/
    private static final String                  PASS                 = "123";
    /***/
    private static final String                  TENANT               = "ntua";
    /***/
    private static final String                  TEST_CONTAINER       = "test-1";
    /***/
    private static final String                  USER                 = "bill";
    /***/
    private final VisionHTTPClient               client               = new VisionHTTPClient(HOST_URL, TENANT, USER, PASS);
    /***/
    private final PerOperationHandler            getHandler           = new PerOperationHandler("GET");
    /***/
    private final PerOperationHandler            putHandler           = new PerOperationHandler("PUT");
    /***/
    private final VismoEventRegistry             registry             = new VismoEventRegistry("tcp://" + HOST_URL + ":"
                                                                              + CONSUMERS_PORT);
    /***/
    private String                               testRuleId           = null;


    /**
     * This is a sanity check for the infrastructure.
     * 
     * @throws InterruptedException
     */
    @Test
    public void producersShouldReceiveDefaultObsEvents() throws InterruptedException {
        registry.registerToAll(putHandler);
        registry.registerToAll(getHandler);

        putObject(OBJ_NAME);
        waitForEventsToBeReceived();
        putHandler.shouldHaveReceivedNoEvents(1);

        readObject(OBJ_NAME);
        waitForEventsToBeReceived();
        getHandler.shouldHaveReceivedNoEvents(1);
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
        deleteTestRule();
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
        registry.registerToAll(DEFAULT_RULE_HANDLER);
        submitRule(throughputThresholdRule(5, "my-topic", TENANT, USER));

        putObject(OBJ_NAME);
        waitForEventsToBeReceived();
        DEFAULT_RULE_HANDLER.shouldHaveReceivedNoEvents(1);
    }


    /**
     * 
     */
    private void deleteTestRule() {
        if (testRuleId != null)
            client.removeRule(testRuleId);
    }


    /**
     * @param objName
     */
    private void putObject(final String objName) {
        client.putObject(TENANT, TEST_CONTAINER, objName,
                         "{ \"foo\": \"bar\", \"is-test\": \"true\", \"value\": \"hello-world\" }");
    }


    /**
     * @param objName
     */
    private void readObject(final String objName) {
        client.getObject(TENANT, TEST_CONTAINER, objName);
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
        testRuleId = client.sumbitRule(bean);
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

        bean.setOperation("PUT");
        bean.setFilterUnit(tenant + "," + user);
        bean.setTopic(topic);
        bean.addRequirement("transaction-throughput", ">=", threshold);

        return bean;
    }


    /**
     * @throws InterruptedException
     */
    private static void waitForEventsToBeReceived() throws InterruptedException {
        Thread.sleep(3000);
    }
}
