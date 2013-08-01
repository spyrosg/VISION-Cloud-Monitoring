package endtoend.tests;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.service.ClusterHeadNodeFactory;
import gr.ntua.vision.monitoring.service.VismoService;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;


/**
 *
 */
public class RulesInterchangeTest extends TestCase {
    /**
     *
     */
    private static class UniqueRule {
        /***/
        public final String className;
        /***/
        public final String id;


        /**
         * Constructor.
         * 
         * @param className
         * @param id
         */
        public UniqueRule(final String className, final String id) {
            className.getClass(); // NPE
            id.getClass(); // NPE
            this.className = className;
            this.id = id;
        }


        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final UniqueRule other = (UniqueRule) obj;
            if (className == null) {
                if (other.className != null)
                    return false;
            } else if (!className.equals(other.className))
                return false;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            return true;
        }


        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((className == null) ? 0 : className.hashCode());
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            return result;
        }


        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "UniqueRule [className=" + className + ", id=" + id + "]";
        }
    }

    /***/
    private static final Logger log           = LoggerFactory.getLogger(RulesInterchangeTest.class);
    /***/
    private Client              client;
    /***/
    @SuppressWarnings("serial")
    private final Properties    p1            = new Properties() {
                                                  {
                                                      setProperty("cloud.name", "visioncloud.eu");
                                                      setProperty("cloud.heads", "10.0.2.211, 10.0.2.212");

                                                      setProperty("cluster.name", "vision-1");
                                                      setProperty("cluster.head", "10.0.2.211");

                                                      setProperty("producers.point", "tcp://127.0.0.1:56429");
                                                      setProperty("consumers.port", "56430");

                                                      setProperty("udp.port", "56431");
                                                      setProperty("cluster.head.port", "56432");

                                                      setProperty("cloud.head.port", "56433");

                                                      setProperty("mon.group.addr", "228.5.6.7");
                                                      setProperty("mon.group.port", "12345");
                                                      setProperty("mon.ping.period", "60000");
                                                      setProperty("startup.rules", "");
                                                      setProperty("web.port", "9996");
                                                  }
                                              };
    /***/
    private Properties          p2;
    /***/
    private Properties          p3;
    /***/
    private VismoService        s1;
    /***/
    private VismoService        s2;
    /***/
    private VismoService        s3;
    /** the socket factory. */
    private final ZMQFactory    socketFactory = new ZMQFactory(new ZContext());


    // TODO: testShouldAlsoPropagateRuleDeletions

    /**
     * @throws Exception
     */
    public void testVismoInstancesShouldExchangeRules() throws Exception {
        assertHaveSameRuleSet(getRules(p1), getRules(p2));
        assertHaveSameRuleSet(getRules(p2), getRules(p3));

        final String id = postNewRule(p1);
        log.debug("posted on p1 rule[id] = {}", id);
        final UniqueRule u = new UniqueRule("ThresholdRule", id);

        assertSetContainsRule(getRules(p1), u);
        assertHaveSameRuleSet(getRules(p1), getRules(p2));
        assertHaveSameRuleSet(getRules(p2), getRules(p3));
    }


    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setupConfig();
        setupClient();
        setupVismoServices();
    }


    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        // s1.halt();
        // s2.halt();
        // s3.halt();

        super.tearDown();
    }


    /**
     * @param props
     * @return the set of rules in the specified {@link VismoService}.
     */
    private HashSet<UniqueRule> getRules(final Properties props) {
        final int port = Integer.valueOf(props.getProperty("web.port"));
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> rules = client.resource("http://localhost:" + port + "/rules")
                .accept(MediaType.APPLICATION_JSON).get(List.class);
        final HashSet<UniqueRule> nameSet = new HashSet<UniqueRule>();

        for (final Map<String, Object> rule : rules)
            nameSet.add(new UniqueRule((String) rule.get("class"), (String) rule.get("id")));

        return nameSet;
    }


    /**
     * @param props
     * @return a configured {@link VismoService}.
     * @throws IOException
     */
    private VismoService newVismoService(final Properties props) throws IOException {
        final VismoConfiguration conf = new VismoConfiguration(props);

        return (VismoService) new ClusterHeadNodeFactory(conf, socketFactory).build(new VismoVMInfo());
    }


    /**
     * @param props
     * @return the id of the rule posted.
     */
    private String postNewRule(final Properties props) {
        final int port = Integer.valueOf(props.getProperty("web.port"));
        final ThresholdRuleBean bean = getBean();
        final ClientResponse res = client.resource("http://localhost:" + port + "/rules").type(MediaType.APPLICATION_JSON)
                .entity(bean).post(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());

        return res.getEntity(String.class);
    }


    /***/
    private void setupClient() {
        final DefaultClientConfig cc = new DefaultClientConfig();

        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        client = Client.create(cc);
    }


    /***/
    private void setupConfig() {
        p2 = (Properties) p1.clone();

        p2.setProperty("web.port", "9997");
        p2.setProperty("producers.point", "tcp://127.0.0.1:56479");
        p2.setProperty("cluster.head.port", "56472");
        p2.setProperty("consumers.port", "56470");

        p3 = (Properties) p1.clone();
        p3.setProperty("web.port", "9998");
        p3.setProperty("producers.point", "tcp://127.0.0.1:56480");
        p3.setProperty("cluster.head.port", "56473");
        p3.setProperty("consumers.port", "56471");
    }


    /**
     * @throws IOException
     */
    private void setupVismoServices() throws IOException {
        s1 = newVismoService(p1);
        s1.start();
        s2 = newVismoService(p2);
        s2.start();
        s3 = newVismoService(p3);
        s3.start();
    }


    /**
     * @param s1
     * @param s2
     */
    private static void assertHaveSameRuleSet(final HashSet<UniqueRule> s1, final HashSet<UniqueRule> s2) {
        s1.removeAll(getRulesUniqueToEachInstance(s1));
        s2.removeAll(getRulesUniqueToEachInstance(s2));

        System.out.println("comparing: " + s1 + " against: " + s2);
        assertEquals(s1, s2);
    }


    /**
     * @param set
     * @param r
     */
    private static void assertSetContainsRule(final HashSet<UniqueRule> set, final UniqueRule r) {
        assertTrue("expecting " + r + " to be found in " + set, set.contains(r));
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
     * @param s
     * @return a hash set.
     */
    private static HashSet<UniqueRule> getRulesUniqueToEachInstance(final HashSet<UniqueRule> s) {
        // FIXME: move this to RuleStore probably
        final HashSet<UniqueRule> dontCare = new HashSet<UniqueRule>();

        for (final UniqueRule r : s)
            if ("PassThroughRule".equals(r.className))
                dontCare.add(r);

        System.out.println("init: " + s + ", don't care: " + dontCare);

        return dontCare;
    }
}
