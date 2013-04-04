package integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.resources.RulesResource;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.ThresholdRulesFactory;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.web.WebAppBuilder;
import gr.ntua.vision.monitoring.web.WebServer;

import javax.ws.rs.core.Application;
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
 * This is used to test the addition/deletion of rules through the HTTP interface.
 */
public class RulesResourceTest {
    /***/
    private static final int    PORT       = 9998;
    /***/
    private static final String ROOT_URL   = "http://localhost:" + PORT;
    /***/
    private final Client        client;
    /***/
    private final RulesStore    rulesStore = new RulesStore();
    /***/
    private WebServer           server;

    {
        final ClientConfig cc = new DefaultClientConfig();

        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        client = Client.create(cc);
    }


    /***/
    @Test
    public void putRule() {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        bean.setTopic("my-topic");
        bean.setMetric("latency");
        bean.setPredicate(">");
        bean.setThreshold(1.3);

        final ClientResponse res = root().path("rules").type(MediaType.APPLICATION_JSON).entity(bean).post(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());

        final String id = res.getEntity(String.class);

        assertTrue("rules factory should insert rule in the store", rulesStore.containsById(id));
    }


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        final WebAppBuilder builder = new WebAppBuilder();
        final VismoRulesEngine engine = new VismoRulesEngine(rulesStore);
        final Application rulesApp = builder.addResource(new RulesResource(new ThresholdRulesFactory(engine))).build();

        server = new WebServer(PORT);
        server.withWebAppAt(rulesApp, "/*");

        server.start();
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
}
