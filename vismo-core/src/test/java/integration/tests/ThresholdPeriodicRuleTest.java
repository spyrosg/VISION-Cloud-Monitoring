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
import java.util.HashMap;
import java.util.Random;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * 
 */
public class ThresholdPeriodicRuleTest extends JerseyResourceTest {
    /***/
    private static final String              AVERAGE_THROUGHPUT_TOPIC = "average-throughput-topic";
    /***/
    private static final String              CONTAINER                = "test-container";
    /***/
    private static final int                 NO_EVENTS                = 10;
    /***/
    private static final long                RULE_PERIOD              = 500;
    /***/
    private static final String              TENANT                   = "ntua";
    /***/
    private static final double              THRESHOLD                = 10 * 1024;
    /***/
    private static final String              USER                     = "vassilis";
    /***/
    private VismoRulesEngine                 engine;
    /***/
    private final ArrayList<MonitoringEvent> eventSink                = new ArrayList<MonitoringEvent>();
    /***/
    private ThresholdRulesFactory            factory;
    /***/
    private FakeObjectService                obs;


    /**
     * @throws Exception
     */
    public void testCountNoOperationsPeriodicRule() throws Exception {
        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(countNoWriteOperationsRule(TENANT, USER, CONTAINER));

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals(1, engine.noRules());

        triggerRule();
        Thread.sleep(6 * RULE_PERIOD / 5);
        assertEquals(1, eventSink.size());

        @SuppressWarnings("unchecked")
        final ArrayList<HashMap<String, Object>> violations = (ArrayList<HashMap<String, Object>>) eventSink.get(0)
                .get("violations");

        assertEquals(NO_EVENTS, (Double) violations.get(0).get("value"), 0.005);
    }


    /**
     * @throws InterruptedException
     */
    public void testSubmitRuleShouldProduceEventWithContainerAggregationUnit() throws InterruptedException {
        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(averageThroughputThresholdRule(TENANT, USER, CONTAINER));

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals(1, engine.noRules());

        triggerRule();
        Thread.sleep(6 * RULE_PERIOD / 5);
        assertEquals(1, eventSink.size());
        assertIsExpectedEvent(eventSink.get(0));
    }


    /**
     * @see integration.tests.JerseyResourceTest#resource()
     */
    @Override
    protected WebResource resource() {
        return super.resource().path("rules");
    }


    /**
     * @see integration.tests.JerseyResourceTest#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        engine = new VismoRulesEngine();
        obs = new FakeObjectService(new InMemoryEventDispatcher(engine, "fake-obs"), new Random(3331));
        factory = new ThresholdRulesFactory(engine);
        engine.appendSink(new InMemoryEventSink(eventSink));
        configureServer(WebAppBuilder.buildFrom(new RulesResource(factory, new RulesStore())), "/*");
        startServer();
    }


    /**
     * @param bean
     * @return the {@link ClientResponse}.
     */
    private ClientResponse submitRule(final ThresholdRuleBean bean) {
        return resource().type(MediaType.APPLICATION_JSON).entity(bean).post(ClientResponse.class);
    }


    /***/
    private void triggerRule() {
        for (int i = 0; i < NO_EVENTS; ++i)
            obs.putEvent(TENANT, USER, CONTAINER, "ignored-object-name").send();
    }


    /**
     * @param e
     */
    private static void assertIsExpectedEvent(final MonitoringEvent e) {
        assertNotNull(e);
        assertEquals(AVERAGE_THROUGHPUT_TOPIC, e.topic());
        assertTrue("originating-machine key should be a String", e.get("originating-machine") instanceof String);
    }


    /**
     * @param tenant
     * @param user
     * @param containerName
     * @return a {@link ThresholdRuleBean}.
     */
    private static ThresholdRuleBean averageThroughputThresholdRule(final String tenant, final String user,
            final String containerName) {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        bean.setPeriod(RULE_PERIOD);
        bean.setOperation("PUT");
        bean.setFilterUnit(tenant + "," + user + "," + containerName);
        bean.setTopic(AVERAGE_THROUGHPUT_TOPIC);
        bean.addRequirement("transaction-throughput", "avg", ">=", THRESHOLD);

        return bean;
    }


    /**
     * @param tenant
     * @param user
     * @param containerName
     * @return a {@link ThresholdRuleBean}.
     */
    private static ThresholdRuleBean countNoWriteOperationsRule(final String tenant, final String user, final String containerName) {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        bean.setPeriod(RULE_PERIOD);
        bean.setOperation("PUT");
        bean.setFilterUnit(tenant + "," + user + "," + containerName);
        bean.setTopic("count-writes");
        bean.addRequirement(null, "count", ">=", -1);

        return bean;
    }
}
