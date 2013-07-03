package integration.tests;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.RulesResource;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.ThresholdRulesFactory;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.InMemoryEventSink;
import gr.ntua.vision.monitoring.web.WebAppBuilder;
import helpers.InMemoryEventDispatcher;

import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * This is used to validate that a newly added rule can run and produce events in the rules' engine. The specific rule constructed
 * and submitted generates events when a request's throughput is too high.
 */
public class ThresholdRuleTest extends JerseyResourceTest {
    /***/
    private static final String              CONTAINER = "test-container";
    /***/
    private static final Logger              log       = LoggerFactory.getLogger(ThresholdRuleTest.class);
    /***/
    private static final String              TENANT    = "ntua";
    /***/
    private static final double              THRESHOLD = 5;
    /***/
    private static final String              USER      = "vassilis";
    /***/
    private VismoRulesEngine                 engine;
    /***/
    private final ArrayList<MonitoringEvent> eventSink = new ArrayList<MonitoringEvent>();
    /***/
    private ThresholdRulesFactory            factory;
    /***/
    private FakeObjectService                obs;


    /**
     * @see integration.tests.JerseyResourceTest#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        engine = new VismoRulesEngine();
        obs = new FakeObjectService(new InMemoryEventDispatcher(engine, "fake-obs"));
        factory = new ThresholdRulesFactory(engine);

        engine.appendSink(new InMemoryEventSink(eventSink));
        configureServer(WebAppBuilder.buildFrom(new RulesResource(factory, new RulesStore())), "/*");
        startServer();
    }


    /**
     * 
     */
    public void testShouldProduceMultiRequirementsResult() {
        final String TOPIC = "throughput-latency-topic";
        assertEquals(0, engine.noRules());

        final ThresholdRuleBean bean = new ThresholdRuleBean();

        bean.setOperation("PUT");
        bean.setFilterUnit(TENANT + "," + USER);
        bean.setTopic(TOPIC);
        bean.addRequirement("transaction-throughput", ">=", 1.0);
        bean.addRequirement("transaction-latency", ">=", 0.05);

        final ClientResponse res = submitRule(bean);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals(1, engine.noRules());

        triggerRule();
        assertEquals(1, eventSink.size());
        assertIsExpectedEvent(eventSink.get(0), TOPIC);
    }


    /***/
    public void testShouldRejectSubmittedRuleOfInvalidSpecification() {
        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(invalidSpecificationRule());

        assertEquals(ClientResponse.Status.BAD_REQUEST, res.getClientResponseStatus());
        assertEquals(0, engine.noRules());
    }


    /***/
    public void testSubmittedRuleShouldNotProduceEventWhenNotMatching() {
        final String TOPIC = "throughput-topic";

        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(throughputThresholdRule(TOPIC, TENANT, USER, "-dummy-"));

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals(1, engine.noRules());

        triggerRule();
        assertEquals(0, eventSink.size());
    }


    /***/
    public void testSubmittedRuleShouldProduceEventWithContainerAggregationUnit() {
        final String TOPIC = "throughput-topic";

        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(throughputThresholdRule(TOPIC, TENANT, USER, CONTAINER));

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals(1, engine.noRules());

        triggerRule();
        assertEquals(1, eventSink.size());
        assertIsExpectedEvent(eventSink.get(0), TOPIC);
    }


    /***/
    public void testSubmittedRuleShouldProduceEventWithUserAggregationUnit() {
        final String TOPIC = "throughput-topic";

        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(throughputThresholdRule(TENANT, USER));

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals(1, engine.noRules());

        triggerRule();
        assertEquals(1, eventSink.size());
        assertIsExpectedEvent(eventSink.get(0), TOPIC);
    }


    /**
     * @see integration.tests.JerseyResourceTest#resource()
     */
    @Override
    protected WebResource resource() {
        return root().path("rules");
    }


    /**
     * @param bean
     * @return the {@link ClientResponse}.
     */
    private ClientResponse submitRule(final ThresholdRuleBean bean) {
        return root().path("rules").type(MediaType.APPLICATION_JSON).entity(bean).post(ClientResponse.class);
    }


    /***/
    private void triggerRule() {
        obs.putEvent(TENANT, USER, CONTAINER, "ignored-object-name").send();
    }


    /**
     * @param e
     * @param topic
     */
    private static void assertIsExpectedEvent(final MonitoringEvent e, final String topic) {
        log.debug("asserting event: {}", e);
        assertEquals(topic, e.topic());
        assertTrue("originating-machine key should be a String", e.get("originating-machine") instanceof String);
    }


    /**
     * @return a {@link ThresholdRuleBean}.
     */
    private static ThresholdRuleBean invalidSpecificationRule() {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        bean.setOperation("PUT");
        bean.setFilterUnit(TENANT + "," + USER + "," + CONTAINER);
        bean.setTopic("throughput-topic");
        bean.addRequirement("transaction-throughput", "-my-predicate-", THRESHOLD);

        return bean;
    }


    /**
     * @param tenant
     * @param user
     * @return a {@link ThresholdRuleBean}.
     */
    private static ThresholdRuleBean throughputThresholdRule(final String tenant, final String user) {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        bean.setOperation("PUT");
        bean.setFilterUnit(tenant + "," + user);
        bean.setTopic("throughput-topic");
        bean.addRequirement("transaction-throughput", ">=", THRESHOLD);

        return bean;
    }


    /**
     * @param topic
     * @param tenant
     * @param user
     * @param containerName
     * @return a {@link ThresholdRuleBean}.
     */
    private static ThresholdRuleBean throughputThresholdRule(final String topic, final String tenant, final String user,
            final String containerName) {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        bean.setOperation("PUT");
        bean.setFilterUnit(tenant + "," + user + "," + containerName);
        bean.setTopic(topic);
        bean.addRequirement("transaction-throughput", ">=", THRESHOLD);

        return bean;
    }
}
