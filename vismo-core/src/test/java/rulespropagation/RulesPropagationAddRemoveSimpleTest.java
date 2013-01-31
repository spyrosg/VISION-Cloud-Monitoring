package rulespropagation;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.sinks.EventSinks;
import gr.ntua.vision.monitoring.sinks.InMemoryEventSink;
import gr.ntua.vision.monitoring.sources.InMemoryEventSource;

import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * @author tmessini
 */
public class RulesPropagationAddRemoveSimpleTest {

    /***/
    private static final int                 PORT     = 9998;

    /***/
    private static final String              ROOT_URL = "http://localhost:" + RulesPropagationAddRemoveSimpleTest.PORT;
    /***/
    private final static InMemoryEventSource source   = new InMemoryEventSource();
    /***/
    private final static ArrayList<Event>    store    = new ArrayList<Event>();
    /***/
    final VismoRulesEngine                   engine1  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine10 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine11 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine12 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine13 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine14 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine15 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine2  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine3  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine4  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine5  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine6  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine7  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine8  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine9  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationAddRemoveSimpleTest.store)));
    /***/
    final Logger                             log      = LoggerFactory.getLogger(RulesPropagationAddRemoveSimpleTest.class);
    /***/
    RulesPropagationManager                  rulesManager1;

    /***/
    RulesPropagationManager                  rulesManager10;
    /***/
    RulesPropagationManager                  rulesManager11;
    /***/
    RulesPropagationManager                  rulesManager12;
    /***/
    RulesPropagationManager                  rulesManager13;
    /***/
    RulesPropagationManager                  rulesManager14;
    /***/
    RulesPropagationManager                  rulesManager15;
    /***/
    RulesPropagationManager                  rulesManager2;
    /***/
    RulesPropagationManager                  rulesManager3;
    /***/
    RulesPropagationManager                  rulesManager4;
    /***/
    RulesPropagationManager                  rulesManager5;
    /***/
    RulesPropagationManager                  rulesManager6;
    /***/
    RulesPropagationManager                  rulesManager7;
    /***/
    RulesPropagationManager                  rulesManager8;
    /***/
    RulesPropagationManager                  rulesManager9;
    /***/
    private final Client                     client   = new Client();


    /***/
    @Test
    public void insertRule() {
        ClientResponse res = root().path("rules/AccountingRule/10000/@").accept(MediaType.TEXT_PLAIN).put(ClientResponse.class);
        Assert.assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        // wait in order the message to be propagated and executed
        threadSleep(2000);
        // check if all rule managers have the rule
        Assert.assertEquals(true, rulesManager1.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager2.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager3.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager4.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager5.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager6.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager7.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager8.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager9.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager10.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager11.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager12.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager13.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager14.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(true, rulesManager15.getRuleStore().containsRule("AccountingRule:10000:@"));

        // parse the reply to get the random ruleId
        final String[] reply = res.getEntity(String.class).split(" ");
        String ruleId = null;
        if (reply.length == 4)
            ruleId = reply[2];

        res = root().path("rules/" + ruleId).accept(MediaType.TEXT_PLAIN).delete(ClientResponse.class);
        Assert.assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        // wait in order the message to be propagated and executed
        threadSleep(2000);
        // check that rules was deleted from all rule managers
        Assert.assertEquals(false, rulesManager1.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager2.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager3.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager4.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager5.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager6.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager7.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager8.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager9.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager10.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager11.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager12.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager13.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager14.getRuleStore().containsRule("AccountingRule:10000:@"));
        Assert.assertEquals(false, rulesManager15.getRuleStore().containsRule("AccountingRule:10000:@"));

    }


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        engine1.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine2.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine3.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine4.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine5.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine6.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine7.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine8.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine9.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine10.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine11.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine12.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine13.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine14.registerToSource(RulesPropagationAddRemoveSimpleTest.source);
        engine15.registerToSource(RulesPropagationAddRemoveSimpleTest.source);

        rulesManager1 = new RulesPropagationManager(engine1, "gr.ntua.vision.monitoring.rules.propagation", 9996);
        rulesManager2 = new RulesPropagationManager(engine2, "gr.ntua.vision.monitoring.rules.propagation", 9997);
        rulesManager3 = new RulesPropagationManager(engine3, "gr.ntua.vision.monitoring.rules.propagation", 9998);
        rulesManager4 = new RulesPropagationManager(engine4, "gr.ntua.vision.monitoring.rules.propagation", 9999);
        rulesManager5 = new RulesPropagationManager(engine5, "gr.ntua.vision.monitoring.rules.propagation", 10000);
        rulesManager6 = new RulesPropagationManager(engine6, "gr.ntua.vision.monitoring.rules.propagation", 10001);
        rulesManager7 = new RulesPropagationManager(engine7, "gr.ntua.vision.monitoring.rules.propagation", 10002);
        rulesManager8 = new RulesPropagationManager(engine8, "gr.ntua.vision.monitoring.rules.propagation", 10003);
        rulesManager9 = new RulesPropagationManager(engine9, "gr.ntua.vision.monitoring.rules.propagation", 10004);
        rulesManager10 = new RulesPropagationManager(engine10, "gr.ntua.vision.monitoring.rules.propagation", 10005);
        rulesManager11 = new RulesPropagationManager(engine11, "gr.ntua.vision.monitoring.rules.propagation", 10006);
        rulesManager12 = new RulesPropagationManager(engine12, "gr.ntua.vision.monitoring.rules.propagation", 10007);
        rulesManager13 = new RulesPropagationManager(engine13, "gr.ntua.vision.monitoring.rules.propagation", 10008);
        rulesManager14 = new RulesPropagationManager(engine14, "gr.ntua.vision.monitoring.rules.propagation", 10009);
        rulesManager15 = new RulesPropagationManager(engine15, "gr.ntua.vision.monitoring.rules.propagation", 10010);

        rulesManager1.start();
        rulesManager2.start();
        rulesManager3.start();
        rulesManager4.start();
        rulesManager5.start();
        rulesManager6.start();
        rulesManager7.start();
        rulesManager8.start();
        rulesManager9.start();
        rulesManager10.start();
        rulesManager11.start();
        rulesManager12.start();
        rulesManager13.start();
        rulesManager14.start();
        rulesManager15.start();
        threadSleep(3000);
    }


    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        rulesManager1.halt();
        rulesManager2.halt();
        rulesManager3.halt();
        rulesManager4.halt();
        rulesManager5.halt();
        rulesManager6.halt();
        rulesManager7.halt();
        rulesManager8.halt();
        rulesManager9.halt();
        rulesManager10.halt();
        rulesManager11.halt();
        rulesManager12.halt();
        rulesManager13.halt();
        rulesManager14.halt();
        rulesManager15.halt();
    }


    /**
     * @return a web resource pointing to the server's root.
     */
    private WebResource root() {
        return client.resource(RulesPropagationAddRemoveSimpleTest.ROOT_URL);
    }


    /**
     * sleeps in order the messages ADD_RULE, DELETE_RULE to be propagated
     * and also in order the heart beat service to be able to give results 
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
