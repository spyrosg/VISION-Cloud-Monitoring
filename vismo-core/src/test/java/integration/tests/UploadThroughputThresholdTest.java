package integration.tests;

import static org.junit.Assert.assertEquals;
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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;


/**
 * This is used to validate that a newly added rule can run and produce events in the rules' engine. The specific rule constructed
 * and submitted, generates events when a request's throughput is too low.
 */
public class UploadThroughputThresholdTest {
    /***/
    private static final String              CONTAINER = "test-container";
    /***/
    private static final int                 PORT      = 9998;
    /***/
    private static final String              ROOT_URL  = "http://localhost:" + PORT;
    /***/
    private static final String              TENANT    = "ntua";
    /***/
    private static final String              USER      = "vassilis";
    /***/
    private final Client                     client;
    /***/
    private VismoRulesEngine                 engine;
    /***/
    private final ArrayList<MonitoringEvent> eventSink = new ArrayList<MonitoringEvent>();
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


    /***/
    @Test
    public void submitRuleShouldProduceEvent() {
        assertEquals(0, engine.noRules());
        submitThroughputThresholdRule();
        assertEquals(1, engine.noRules());

        obs.putEvent(TENANT, USER, CONTAINER, "ignored-object-name").send();

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


    /***/
    private void submitThroughputThresholdRule() {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        // we're concerned about the upload throughout
        bean.setMetric("transaction-throughput");
        bean.setOperation("PUT");
        // under given container
        bean.setAggregationUnit(TENANT + "," + USER + "," + CONTAINER);
        // if it's lower
        bean.setPredicate("<=");
        // than 5 bytes / second
        bean.setThreshold(5);
        // generate event with given topic.
        bean.setTopic("throughput-topic");

        final ClientResponse res = root().path("rules").type(MediaType.APPLICATION_JSON).entity(bean).post(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
    }


    /**
     * @param e
     */
    private static void assertIsExpectedEvent(final MonitoringEvent e) {
        assertEquals("throughput-topic", e.topic());
    }
}
