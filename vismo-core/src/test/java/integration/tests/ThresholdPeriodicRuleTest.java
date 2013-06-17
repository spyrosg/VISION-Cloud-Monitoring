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
import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;


/**
 * 
 */
public class ThresholdPeriodicRuleTest extends JerseyResourceTest {
    /***/
    private static final String              AVERAGE_THROUGHPUT_TOPIC = "average-throughput-topic";
    /***/
    private static final String              AVG_AGGREGATION_METHOD   = "avg";
    /***/
    private static final String              CONTAINER                = "test-container";
    /***/
    private static final Logger              log                      = LoggerFactory.getLogger(ThresholdPeriodicRuleTest.class);
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
     * @see integration.tests.JerseyResourceTest#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        engine = new VismoRulesEngine();
        obs = new FakeObjectService(new InMemoryEventDispatcher(engine, "fake-obs"), new Random(3331));
        factory = new ThresholdRulesFactory(engine);
        engine.appendSink(new InMemoryEventSink(eventSink));
        configureServer(WebAppBuilder.buildFrom(new RulesResource(factory, new RulesStore())), "/*");
        startServer();
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
     * @param bean
     * @return the {@link ClientResponse}.
     */
    private ClientResponse submitRule(final ThresholdRuleBean bean) {
        return root().path("rules").type(MediaType.APPLICATION_JSON).entity(bean).post(ClientResponse.class);
    }


    /***/
    private void triggerRule() {
        for (int i = 0; i < NO_EVENTS; ++i)
            obs.putEvent(TENANT, USER, CONTAINER, "ignored-object-name").send();
    }


    /**
     * @param e
     */
    @SuppressWarnings("null")
    private static void assertIsExpectedEvent(final MonitoringEvent e) {
        log.debug("asserting event: {}", e);
        assertTrue("event cannot be null", e != null);
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
        bean.addRequirement("transaction-throughput", AVG_AGGREGATION_METHOD, ">=", THRESHOLD);

        return bean;
    }
}
