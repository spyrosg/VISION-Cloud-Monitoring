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


    /***/
    public void testShouldRejectSubmittedRuleOfInvalidSpecification() {
        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(invalidSpecificationRule());

        assertEquals(ClientResponse.Status.BAD_REQUEST, res.getClientResponseStatus());
        assertEquals(0, engine.noRules());
    }


    /***/
    public void testSubmittedRuleShouldNotProduceEventWhenNotMatching() {
        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(throughputThresholdRule(TENANT, USER, "-dummy-"));

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals(1, engine.noRules());

        triggerRule();
        assertEquals(0, eventSink.size());
    }


    /***/
    public void testSubmittedRuleShouldProduceEventWithContainerAggregationUnit() {
        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(throughputThresholdRule(TENANT, USER, CONTAINER));

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals(1, engine.noRules());

        triggerRule();
        assertEquals(1, eventSink.size());
        assertIsExpectedEvent(eventSink.get(0));
    }


    /***/
    public void testSubmittedRuleShouldProduceEventWithUserAggregationUnit() {
        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(throughputThresholdRule(TENANT, USER));

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals(1, engine.noRules());

        triggerRule();
        assertEquals(1, eventSink.size());
        assertIsExpectedEvent(eventSink.get(0));
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
     */
    private static void assertIsExpectedEvent(final MonitoringEvent e) {
        log.debug("asserting event: {}", e);
        assertEquals("throughput-topic", e.topic());
        assertTrue((Double) e.get("value") >= THRESHOLD);
        assertTrue("originating-machine key should be a String", e.get("originating-machine") instanceof String);
    }


    /**
     * @return a {@link ThresholdRuleBean}.
     */
    private static ThresholdRuleBean invalidSpecificationRule() {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        // we're concerned about the upload throughout
        bean.setMetric("transaction-throughput");
        bean.setOperation("PUT");
        // under given container
        bean.setFilterUnit(TENANT + "," + USER + "," + CONTAINER);
        // if it's lower
        bean.setPredicate("-my-predicate-");
        // than 5 bytes / second
        bean.setThreshold(THRESHOLD);
        // generate event with given topic.
        bean.setTopic("throughput-topic");

        return bean;
    }


    /**
     * @param tenant
     * @param user
     * @return a {@link ThresholdRuleBean}.
     */
    private static ThresholdRuleBean throughputThresholdRule(final String tenant, final String user) {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        // we're concerned about the upload throughout
        bean.setMetric("transaction-throughput");
        bean.setOperation("PUT");
        // under given container
        bean.setFilterUnit(tenant + "," + user);
        // if it's higher...
        bean.setPredicate(">=");
        // than THRESHOLD bytes / second
        bean.setThreshold(THRESHOLD);
        // generate event with given topic.
        bean.setTopic("throughput-topic");

        return bean;
    }


    /**
     * @param tenant
     * @param user
     * @param containerName
     * @return a {@link ThresholdRuleBean}.
     */
    private static ThresholdRuleBean throughputThresholdRule(final String tenant, final String user, final String containerName) {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        // we're concerned about the upload throughout
        bean.setMetric("transaction-throughput");
        bean.setOperation("PUT");
        // under given container
        bean.setFilterUnit(tenant + "," + user + "," + containerName);
        // if it's lower
        bean.setPredicate(">=");
        // than threshold bytes / second
        bean.setThreshold(THRESHOLD);
        // generate event with given topic.
        bean.setTopic("throughput-topic");

        return bean;
    }
}
