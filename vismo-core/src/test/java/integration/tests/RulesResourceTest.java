package integration.tests;

import gr.ntua.vision.monitoring.resources.RulesResource;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.rules.ClassPathRulesFactory;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.ThresholdRulesFactory;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.web.WebAppBuilder;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;


/**
 * This is used to test the addition/deletion of rules through the HTTP interface.
 */
public class RulesResourceTest extends JerseyResourceTest {
    /***/
    private final RulesStore rulesStore = new RulesStore();


    /**
     * @see integration.tests.JerseyResourceTest#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        final VismoRulesEngine engine = new VismoRulesEngine(rulesStore);
        final ClassPathRulesFactory clsPathfactory = new ClassPathRulesFactory(engine, PassThroughRule.class.getPackage());
        final ThresholdRulesFactory factory = new ThresholdRulesFactory(clsPathfactory, engine);
        final Application rulesApp = WebAppBuilder.buildFrom(new RulesResource(factory, rulesStore));

        configureServer(rulesApp, "/*");
        startServer();
    }


    /***/
    public void testHttpDELETEShouldRemoveExistingRuleFromStore() {
        final ClientResponse res = root().path("rules").path("AccountingRule").path("10000").post(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());

        final String id = res.getEntity(String.class);

        final ClientResponse res1 = deleteRule(id);

        assertEquals(ClientResponse.Status.NO_CONTENT, res1.getClientResponseStatus());
    }


    /***/
    public void testHttpGETShouldListStoredRules() {
        postRule(getBean());
        postRule("AccountingRule", 30 * 1000);
        postRule("StorletLoggingRule", 60 * 1000);
        postRule(getBean(45 * 1000));

        final ClientResponse res = getRules();

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        assertEquals(4, rulesStore.size());
        System.out.println(res.getEntity(String.class));
    }


    /***/
    public void testHttpPOSTSholdStoreDefaultRule() {
        final ClientResponse res = postRule("AccountingRule", 10000);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());

        final String id = res.getEntity(String.class);

        assertTrue("rules factory should insert rule in the store", rulesStore.containsById(id));
    }


    /***/
    public void testHttpPOSTShouldStoreThresholdRule() {
        final ThresholdRuleBean bean = getBean();
        final ClientResponse res = postRule(bean);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());

        final String id = res.getEntity(String.class);

        assertTrue("rules factory should insert rule in the store", rulesStore.containsById(id));
    }


    /***/
    public void testHttpPUTShouldUpdateRule() {
        // TODO
    }


    /**
     * @param ruleId
     * @return the {@link ClientResponse}.
     */
    private ClientResponse deleteRule(final String ruleId) {
        return root().path("rules").path(ruleId).accept(MediaType.TEXT_PLAIN).delete(ClientResponse.class);
    }


    /**
     * @return the {@link ClientResponse}.
     */
    private ClientResponse getRules() {
        return root().path("rules").get(ClientResponse.class);
    }


    /**
     * @param name
     * @param period
     * @return the {@link ClientResponse}.
     */
    private ClientResponse postRule(final String name, final long period) {
        return root().path("rules").path(name).path(String.valueOf(period)).post(ClientResponse.class);
    }


    /**
     * @param bean
     * @return the {@link ClientResponse}.
     */
    private ClientResponse postRule(final ThresholdRuleBean bean) {
        return root().path("rules").type(MediaType.APPLICATION_JSON).entity(bean).post(ClientResponse.class);
    }


    /**
     * @return a {@link ThresholdRuleBean}.
     */
    private static ThresholdRuleBean getBean() {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        bean.setTopic("my-topic");
        bean.setMetric("latency");
        bean.setPredicate(">");
        bean.setThreshold(1.3);

        return bean;
    }


    /**
     * @param period
     * @return a {@link ThresholdRuleBean}.
     */
    private static ThresholdRuleBean getBean(final long period) {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        bean.setTopic("my-topic");
        bean.setMetric("latency");
        bean.setPredicate(">");
        bean.setThreshold(1.3);
        bean.setPeriod(period);
        bean.setAggregationMethod("sum");

        return bean;
    }
}
