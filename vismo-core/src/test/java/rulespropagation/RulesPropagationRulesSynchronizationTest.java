package rulespropagation;

import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * @author tmessini
 */
@Ignore("not working")
public class RulesPropagationRulesSynchronizationTest {
    /***/
    private static final int                         GROUP1_SIZE          = 10;
    /***/
    private static final int                         GROUP2_SIZE          = 5;
    /***/
    private final static int                         PROBE_PORT           = RulesPropagationRulesSynchronizationTest
                                                                                  .getProbePort();
    /***/
    private static final String                      ROOT_URL             = "http://localhost:"
                                                                                  + RulesPropagationRulesSynchronizationTest.PROBE_PORT;
    /***/
    private static final int                         WEBSERVER_START_PORT = 9996;
    /***/
    private final Client                             client               = new Client();
    /***/
    private final ArrayList<RulesPropagationManager> managers             = new ArrayList<RulesPropagationManager>();


    /**
     * we check if the addition of a rule in a cluster through the instance that we use as a probe is propagated in a second group
     * that enters the cluster later. In order the rule to be propagated to the new group of cluster: 1.a node to be elected 2.the
     * elected node should start the synchronization process 3.valid rule set should be elected and propagated 4.all instances
     * should check and impose the elected rule set
     * 
     * @throws IOException
     */
    @Test
    public void ruleSynchronizationTest() throws IOException {
        final ClientResponse res = root().path("rules/AccountingRule/10000/@").accept(MediaType.TEXT_PLAIN)
                .put(ClientResponse.class);
        Assert.assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        // wait in order the message to be propagated and executed
        threadSleep(2000);
        // check if all rule managers have the rule in group1
        for (int i = 0; i < RulesPropagationRulesSynchronizationTest.GROUP1_SIZE; i++)
            Assert.assertEquals(true, managers.get(i).getRuleStore().containsRule("AccountingRule:10000:@"));

        // start group 2
        for (int i = RulesPropagationRulesSynchronizationTest.GROUP1_SIZE; i < RulesPropagationRulesSynchronizationTest.GROUP1_SIZE
                + RulesPropagationRulesSynchronizationTest.GROUP2_SIZE; i++) {
            managers.add(new RulesPropagationManager(new VismoRulesEngine(),
                    RulesPropagationRulesSynchronizationTest.WEBSERVER_START_PORT + i));
            managers.get(i).start();
        }

        int countsecs = 0;
        // wait till the second group get the rule but not more than 120 secs
        while (managers.get(GROUP1_SIZE).getRuleStore().getRulesCatalogSize() == 0 && countsecs < 120) {
            countsecs++;
            threadSleep(1000);
        }

        for (int i = 0; i < RulesPropagationRulesSynchronizationTest.GROUP1_SIZE
                + RulesPropagationRulesSynchronizationTest.GROUP2_SIZE; i++)
            Assert.assertEquals(true, managers.get(i).getRuleStore().containsRule("AccountingRule:10000:@"));
    }


    /**
     * start group 1
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < RulesPropagationRulesSynchronizationTest.GROUP1_SIZE; i++) {
            managers.add(new RulesPropagationManager(new VismoRulesEngine(),
                    RulesPropagationRulesSynchronizationTest.WEBSERVER_START_PORT + i));
            managers.get(i).start();
        }
        threadSleep(3000);
    }


    /**
     * shutdowns the managers
     */
    @After
    public void tearDown() {
        for (int i = 0; i < RulesPropagationRulesSynchronizationTest.GROUP1_SIZE
                + RulesPropagationRulesSynchronizationTest.GROUP2_SIZE; i++)
            managers.get(i).halt();
    }


    /**
     * @return a web resource pointing to the server's root.
     */
    private WebResource root() {
        return client.resource(RulesPropagationRulesSynchronizationTest.ROOT_URL);
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


    /**
     * returns the port we going to use as probe
     * 
     * @return a random valid port
     */
    private static int getProbePort() {
        final Random rand = new Random();
        return rand.nextInt(RulesPropagationRulesSynchronizationTest.GROUP1_SIZE)
                + RulesPropagationRulesSynchronizationTest.WEBSERVER_START_PORT;
    }
}
