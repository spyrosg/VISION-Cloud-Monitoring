package integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.RulesResource;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.rules.VismoRulesFactory;
import gr.ntua.vision.monitoring.sinks.InMemoryEventSink;
import gr.ntua.vision.monitoring.web.WebServer;
import helpers.InMemoryEventDispatcher;

import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;


/**
 * 
 */
public class ThresholdPeriodicRuleTest {
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
    private static final int                 PORT                     = 9998;
    /***/
    private static final String              ROOT_URL                 = "http://localhost:" + PORT;
    /***/
    private static final long                RULE_PERIOD              = 500;
    /***/
    private static final String              TENANT                   = "ntua";
    /***/
    private static final double              THRESHOLD                = 10 * 1024;
    /***/
    private static final String              USER                     = "vassilis";
    /***/
    private final Client                     client;
    /***/
    private VismoRulesEngine                 engine;
    /***/
    private final ArrayList<MonitoringEvent> eventSink                = new ArrayList<MonitoringEvent>();
    /***/
    private VismoRulesFactory                factory;
    /***/
    private FakeObjectService                obs;
    /***/
    private WebServer                        server;

    {
        final ClientConfig cc = new DefaultClientConfig();

        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        client = Client.create(cc);
    }


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        engine = new VismoRulesEngine();
        obs = new FakeObjectService(new InMemoryEventDispatcher(engine));
        factory = new VismoRulesFactory(engine);

        engine.appendSink(new InMemoryEventSink(eventSink));

        server = new WebServer(PORT);
        server.withResource(new RulesResource(factory)).build("/*");
        server.start();
    }


    /**
     * @throws InterruptedException
     */
    @Test
    public void submitRuleShouldProduceEventWithContainerAggregationUnit() throws InterruptedException {
        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(averageThroughputThresholdRule(TENANT, USER, CONTAINER));

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals(1, engine.noRules());

        triggerRule();
        Thread.sleep(RULE_PERIOD);
        assertEquals(1, eventSink.size());
        assertIsExpectedEvent(eventSink.get(0));
    }


    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (server != null)
            server.stop();
    }


    /**
     * @return a web resource pointing to the server's root.
     */
    private WebResource root() {
        return client.resource(ROOT_URL);
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
    private static void assertIsExpectedEvent(final MonitoringEvent e) {
        log.debug("asserting event: {}", e);
        assertEquals(AVERAGE_THROUGHPUT_TOPIC, e.topic());
        assertTrue((Double) e.get("value") >= THRESHOLD);
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

        // we're concerned about the average upload throughout
        bean.setPeriod(RULE_PERIOD);
        bean.setMetric("transaction-throughput");
        bean.setOperation("PUT");
        bean.setAggregationMethod(AVG_AGGREGATION_METHOD);
        // under given container
        bean.setAggregationUnit(tenant + "," + user + "," + containerName);
        // if it's higher...
        bean.setPredicate(">=");
        // than THRESHOLD bytes / second
        bean.setThreshold(THRESHOLD);
        // generate event with given topic.
        bean.setTopic(AVERAGE_THROUGHPUT_TOPIC);

        return bean;
    }
}
