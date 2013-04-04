package integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.resources.RulesResource;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.rules.ClassPathRulesFactory;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
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
    public void httpDELETEShouldRemoveExistingRuleFromStore() {
        final ClientResponse res = root().path("rules").path("AccountingRule").path("10000").post(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());

        final String id = res.getEntity(String.class);

        final ClientResponse res1 = deleteRule(id);

        assertEquals(ClientResponse.Status.NO_CONTENT, res1.getClientResponseStatus());
    }


    /***/
    @Test
    public void httpGETShouldListStoredRules() {
        final ClientResponse res = getRules();

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        System.out.println(res.getEntity(String.class));
    }


    /***/
    @Test
    public void httpPOSTSholdStoreDefaultRule() {
        final ClientResponse res = root().path("rules").path("AccountingRule").path("10000").post(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());

        final String id = res.getEntity(String.class);

        assertTrue("rules factory should insert rule in the store", rulesStore.containsById(id));
    }


    /***/
    @Test
    public void httpPOSTShouldStoreThresholdRule() {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        bean.setTopic("my-topic");
        bean.setMetric("latency");
        bean.setPredicate(">");
        bean.setThreshold(1.3);

        final ClientResponse res = postRule(bean);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());

        final String id = res.getEntity(String.class);

        assertTrue("rules factory should insert rule in the store", rulesStore.containsById(id));
    }


    /***/
    @Test
    public void httpPUTShouldUpdateRule() {
        throw new AssertionError("TODO");
    }


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        final WebAppBuilder builder = new WebAppBuilder();
        final VismoRulesEngine engine = new VismoRulesEngine(rulesStore);
        final ClassPathRulesFactory clsPathfactory = new ClassPathRulesFactory(engine, PassThroughRule.class.getPackage());
        final ThresholdRulesFactory factory = new ThresholdRulesFactory(clsPathfactory, engine);
        final Application rulesApp = builder.addResource(new RulesResource(factory, rulesStore)).build();

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
     * @param ruleId
     * @return
     */
    private ClientResponse deleteRule(final String ruleId) {
        return root().path("rules").path(ruleId).type(MediaType.TEXT_PLAIN).delete(ClientResponse.class);
    }


    /**
     * @return
     */
    private ClientResponse getRules() {
        return root().path("rules").get(ClientResponse.class);
    }


    /**
     * @param bean
     * @return
     */
    private ClientResponse postRule(final ThresholdRuleBean bean) {
        return root().path("rules").type(MediaType.APPLICATION_JSON).entity(bean).post(ClientResponse.class);
    }


    /**
     * @return a web resource pointing to the server's root.
     */
    private WebResource root() {
        return client.resource(ROOT_URL);
    }
}
