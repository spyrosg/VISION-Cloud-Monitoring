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
import com.sun.jersey.api.client.WebResource;


/**
 * This is used to test the addition/deletion of rules through the HTTP interface.
 */
public class RulesResourceTest extends JerseyResourceTest {
    /***/
    private final RulesStore rulesStore = new RulesStore();


    /***/
    public void testHttpDELETEShouldRemoveExistingRuleFromStore() {
        final ClientResponse res = resource().path("AccountingRule").path("10000").post(ClientResponse.class);

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
    public void testHttpPUTShouldUpdateMetricsRulePeriod() {
        final long oldPeriod = 5000;
        final long newPeriod = 10000;

        final String insertedRuleId = postMetricsRule(oldPeriod).getEntity(String.class);

        final ClientResponse res = resource().path(insertedRuleId).path("period").path(String.valueOf(newPeriod))
                .put(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
    }


    /***/
    public void testHttpPUTShouldUpdateRulesFilterUnit() {
        final long period = 5000;
        final String insertedRuleId = postRule(getBean(period)).getEntity(String.class);

        final ClientResponse res = resource().path(insertedRuleId).path("filterUnit").path(String.valueOf("ntua,vassilis,bar"))
                .put(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
    }


    /***/
    public void testHttpPUTShouldUpdateRulesPeriod() {
        final long oldPeriod = 5000;
        final long newPeriod = 10000;

        final String insertedRuleId = postRule(getBean(oldPeriod)).getEntity(String.class);

        final ClientResponse res = resource().path(insertedRuleId).path("period").path(String.valueOf(newPeriod))
                .put(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
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

        final VismoRulesEngine engine = new VismoRulesEngine(rulesStore);
        final ClassPathRulesFactory clsPathfactory = new ClassPathRulesFactory(engine, PassThroughRule.class.getPackage());
        final ThresholdRulesFactory factory = new ThresholdRulesFactory(clsPathfactory, engine);
        final Application rulesApp = WebAppBuilder.buildFrom(new RulesResource(PORT, factory, rulesStore));

        configureServer(rulesApp, "/*");
        startServer();
    }


    /**
     * @param ruleId
     * @return the {@link ClientResponse}.
     */
    private ClientResponse deleteRule(final String ruleId) {
        return resource().path(ruleId).accept(MediaType.TEXT_PLAIN).delete(ClientResponse.class);
    }


    /**
     * @return the {@link ClientResponse}.
     */
    private ClientResponse getRules() {
        return resource().get(ClientResponse.class);
    }


    /**
     * @param period
     * @return
     */
    private ClientResponse postMetricsRule(final long period) {
        return postRule("MetricsRule", period);
    }


    /**
     * @param name
     * @param period
     * @return the {@link ClientResponse}.
     */
    private ClientResponse postRule(final String name, final long period) {
        return resource().path(name).path(String.valueOf(period)).post(ClientResponse.class);
    }


    /**
     * @param bean
     * @return the {@link ClientResponse}.
     */
    private ClientResponse postRule(final ThresholdRuleBean bean) {
        return resource().type(MediaType.APPLICATION_JSON).entity(bean).post(ClientResponse.class);
    }


    /**
     * @return a {@link ThresholdRuleBean}.
     */
    private static ThresholdRuleBean getBean() {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        bean.setTopic("my-topic");
        bean.addRequirement("latency", ">", 1.3);

        return bean;
    }


    /**
     * @param period
     * @return a {@link ThresholdRuleBean}.
     */
    private static ThresholdRuleBean getBean(final long period) {
        final ThresholdRuleBean bean = new ThresholdRuleBean();

        bean.setTopic("my-topic");
        bean.setPeriod(period);
        bean.setFilterUnit("ntua,vassilis,foo");
        bean.addRequirement("latency", "sum", ">", 1.3);

        return bean;
    }
}
