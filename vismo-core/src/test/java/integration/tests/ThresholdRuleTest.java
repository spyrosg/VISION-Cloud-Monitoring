package integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.RulesResource;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.ThresholdRulesFactory;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.InMemoryEventSink;
import gr.ntua.vision.monitoring.web.WebAppBuilder;
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
 * This is used to validate that a newly added rule can run and produce events in the rules' engine. The specific rule constructed
 * and submitted generates events when a request's throughput is too high.
 */
public class ThresholdRuleTest {
    /***/
    private static final String              CONTAINER = "test-container";
    /***/
    private static final Logger              log       = LoggerFactory.getLogger(ThresholdRuleTest.class);
    /***/
    private static final int                 PORT      = 9998;
    /***/
    private static final String              ROOT_URL  = "http://localhost:" + PORT;
    /***/
    private static final String              TENANT    = "ntua";
    /***/
    private static final double              THRESHOLD = 5;
    /***/
    private static final String              USER      = "vassilis";
    /***/
    private final Client                     client;
    /***/
    private VismoRulesEngine                 engine;
    /***/
    private final ArrayList<MonitoringEvent> eventSink = new ArrayList<MonitoringEvent>();
    /***/
    private ThresholdRulesFactory            factory;
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
        factory = new ThresholdRulesFactory(engine);

        engine.appendSink(new InMemoryEventSink(eventSink));

        server = new WebServer(PORT);
        server.withWebAppAt(WebAppBuilder.buildFrom(new RulesResource(factory, new RulesStore())), "/*");
        server.start();
    }


    /***/
    @Test
    public void shouldRejectSubmittedRuleOfInvalidSpecification() {
        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(invalidSpecificationRule());

        assertEquals(ClientResponse.Status.BAD_REQUEST, res.getClientResponseStatus());
        assertEquals(0, engine.noRules());
    }


    /***/
    @Test
    public void submittedRuleShouldNotProduceEventWhenNotMatching() {
        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(throughputThresholdRule(TENANT, USER, "-dummy-"));

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals(1, engine.noRules());

        triggerRule();
        assertEquals(0, eventSink.size());
    }


    /***/
    @Test
    public void submittedRuleShouldProduceEventWithContainerAggregationUnit() {
        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(throughputThresholdRule(TENANT, USER, CONTAINER));

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals(1, engine.noRules());

        triggerRule();
        assertEquals(1, eventSink.size());
        assertIsExpectedEvent(eventSink.get(0));
    }


    /***/
    @Test
    public void submittedRuleShouldProduceEventWithUserAggregationUnit() {
        assertEquals(0, engine.noRules());

        final ClientResponse res = submitRule(throughputThresholdRule(TENANT, USER));

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals(1, engine.noRules());

        triggerRule();
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
        obs.putEvent(TENANT, USER, CONTAINER, "ignored-object-name").send();
    }


    /**
     * @param e
     */
    private static void assertIsExpectedEvent(final MonitoringEvent e) {
        log.debug("asserting event: {}", e);
        assertEquals("throughput-topic", e.topic());
        assertTrue((Double) e.get("value") >= THRESHOLD);
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
        bean.setAggregationUnit(TENANT + "," + USER + "," + CONTAINER);
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
        bean.setAggregationUnit(tenant + "," + user);
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
        bean.setAggregationUnit(tenant + "," + user + "," + containerName);
        // if it's lower
        bean.setPredicate(">=");
        // than threshold bytes / second
        bean.setThreshold(THRESHOLD);
        // generate event with given topic.
        bean.setTopic("throughput-topic");

        return bean;
    }
}
