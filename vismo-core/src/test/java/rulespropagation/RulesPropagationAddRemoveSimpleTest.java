package rulespropagation;

import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.sinks.EventSinks;

import java.util.ArrayList;
import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * @author tmessini
 */
public class RulesPropagationAddRemoveSimpleTest {

    /***/
    private static final int                         GROUP_SIZE           = 20;
    /***/
    private final static int                         PROBE_PORT           = RulesPropagationAddRemoveSimpleTest.getProbePort();
    /***/
    private static final String                      ROOT_URL             = "http://localhost:"
                                                                                  + RulesPropagationAddRemoveSimpleTest.PROBE_PORT;
    /***/
    private static final int                         WEBSERVER_START_PORT = 9996;
    /***/
    private final Client                             client               = new Client();
    /***/
    private final ArrayList<RulesPropagationManager> managers             = new ArrayList<RulesPropagationManager>();


    /**
     * returns the port we are going to use as probe
     * 
     * @return a random valid port
     */
    private static int getProbePort() {
        final Random rand = new Random();
        return rand.nextInt(RulesPropagationAddRemoveSimpleTest.GROUP_SIZE)
                + RulesPropagationAddRemoveSimpleTest.WEBSERVER_START_PORT;
    }


    /**
     * we check if the addition and removal of a rule in a cluster through the instance that we use as a probe is done in a
     * uniform way in all instances of group
     **/
    @Test
    public void addRemoveRuleTest() {
        ClientResponse res = root().path("rules/AccountingRule/10000/@").accept(MediaType.TEXT_PLAIN).put(ClientResponse.class);
        Assert.assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        // wait in order the message to be propagated and executed
        threadSleep(2000);
        // check if all rule managers have the rule
        for (int i = 0; i < RulesPropagationAddRemoveSimpleTest.GROUP_SIZE; i++)
            Assert.assertEquals(true, managers.get(i).getRuleStore().containsRule("AccountingRule:10000:@"));

        res = root().path("rules/" + getRuleId(res.getEntity(String.class))).accept(MediaType.TEXT_PLAIN)
                .delete(ClientResponse.class);
        Assert.assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        // wait in order the message to be propagated and executed
        threadSleep(2000);
        // check that rules was deleted from all rule managers of the group
        for (int i = 0; i < RulesPropagationAddRemoveSimpleTest.GROUP_SIZE; i++)
            Assert.assertEquals(false, managers.get(i).getRuleStore().containsRule("AccountingRule:10000:@"));
    }


    /**
     * starts managers and waits in order the system to stabilize.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < RulesPropagationAddRemoveSimpleTest.GROUP_SIZE; i++) {
            managers.add(new RulesPropagationManager(new VismoRulesEngine(new RulesStore(), new EventSinks()),
                    RulesPropagationAddRemoveSimpleTest.WEBSERVER_START_PORT + i));
            managers.get(i).start();
        }
        threadSleep(3000);
    }


    /**
     * shutdowns the managers
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        for (int i = 0; i < RulesPropagationAddRemoveSimpleTest.GROUP_SIZE; i++)
            managers.get(i).halt();
    }


    /**
     * parse the reply to get the ruleId
     * 
     * @param res
     * @return the ruleId
     */
    @SuppressWarnings("static-method")
    private String getRuleId(final String res) {
        final String[] reply = res.split(" ");
        String ruleId = null;
        if (reply.length == 4)
            ruleId = reply[2];
        return ruleId;
    }


    /**
     * @return a web resource pointing to the server's root.
     */
    private WebResource root() {
        return client.resource(RulesPropagationAddRemoveSimpleTest.ROOT_URL);
    }


    /**
     * sleeps in order the messages ADD_RULE, DELETE_RULE to be propagated and also in order the heart beat service to be able to
     * give results
     * 
     * @param milliseconds
     */
    @SuppressWarnings("static-method")
    private void threadSleep(final int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}
