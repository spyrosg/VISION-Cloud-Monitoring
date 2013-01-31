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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * @author tmessini
 */
public class RulesPropagationSimpleTest {

    /***/
    private static final int                 PORT     = 9998;
    /***/
    private static final String              ROOT_URL = "http://localhost:" + RulesPropagationSimpleTest.PORT;
    /***/
    private final static InMemoryEventSource source   = new InMemoryEventSource();
    /** this is where the events should end up. */
    private final static ArrayList<Event>    store    = new ArrayList<Event>();
    /***/
    final VismoRulesEngine                   engine1  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine10 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine11 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine12 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine13 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine14 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine15 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine2  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine3  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine4  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine5  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine6  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine7  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine8  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
    /***/
    final VismoRulesEngine                   engine9  = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(
                                                              RulesPropagationSimpleTest.store)));
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
        final ClientResponse res = root().path("rules/AccountintRule/10000/@").accept(MediaType.TEXT_PLAIN)
                .get(ClientResponse.class);
        Assert.assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        System.out.println(res.getEntity(String.class));
    }


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        engine1.registerToSource(RulesPropagationSimpleTest.source);
        engine2.registerToSource(RulesPropagationSimpleTest.source);
        engine3.registerToSource(RulesPropagationSimpleTest.source);
        engine4.registerToSource(RulesPropagationSimpleTest.source);
        engine5.registerToSource(RulesPropagationSimpleTest.source);
        engine6.registerToSource(RulesPropagationSimpleTest.source);
        engine7.registerToSource(RulesPropagationSimpleTest.source);
        engine8.registerToSource(RulesPropagationSimpleTest.source);
        engine9.registerToSource(RulesPropagationSimpleTest.source);
        engine10.registerToSource(RulesPropagationSimpleTest.source);
        engine11.registerToSource(RulesPropagationSimpleTest.source);
        engine12.registerToSource(RulesPropagationSimpleTest.source);
        engine13.registerToSource(RulesPropagationSimpleTest.source);
        engine14.registerToSource(RulesPropagationSimpleTest.source);
        engine15.registerToSource(RulesPropagationSimpleTest.source);

        final RulesPropagationManager rulesManager1 = new RulesPropagationManager(engine1,
                "gr.ntua.vision.monitoring.rules.propagation", 9996);
        final RulesPropagationManager rulesManager2 = new RulesPropagationManager(engine2,
                "gr.ntua.vision.monitoring.rules.propagation", 9997);
        final RulesPropagationManager rulesManager3 = new RulesPropagationManager(engine3,
                "gr.ntua.vision.monitoring.rules.propagation", 9998);
        final RulesPropagationManager rulesManager4 = new RulesPropagationManager(engine4,
                "gr.ntua.vision.monitoring.rules.propagation", 9999);
        final RulesPropagationManager rulesManager5 = new RulesPropagationManager(engine5,
                "gr.ntua.vision.monitoring.rules.propagation", 10000);
        final RulesPropagationManager rulesManager6 = new RulesPropagationManager(engine6,
                "gr.ntua.vision.monitoring.rules.propagation", 10001);
        final RulesPropagationManager rulesManager7 = new RulesPropagationManager(engine7,
                "gr.ntua.vision.monitoring.rules.propagation", 10002);
        final RulesPropagationManager rulesManager8 = new RulesPropagationManager(engine8,
                "gr.ntua.vision.monitoring.rules.propagation", 10003);
        final RulesPropagationManager rulesManager9 = new RulesPropagationManager(engine9,
                "gr.ntua.vision.monitoring.rules.propagation", 10004);
        final RulesPropagationManager rulesManager10 = new RulesPropagationManager(engine10,
                "gr.ntua.vision.monitoring.rules.propagation", 10005);
        final RulesPropagationManager rulesManager11 = new RulesPropagationManager(engine11,
                "gr.ntua.vision.monitoring.rules.propagation", 10006);
        final RulesPropagationManager rulesManager12 = new RulesPropagationManager(engine12,
                "gr.ntua.vision.monitoring.rules.propagation", 10007);
        final RulesPropagationManager rulesManager13 = new RulesPropagationManager(engine13,
                "gr.ntua.vision.monitoring.rules.propagation", 10008);
        final RulesPropagationManager rulesManager14 = new RulesPropagationManager(engine14,
                "gr.ntua.vision.monitoring.rules.propagation", 10009);
        final RulesPropagationManager rulesManager15 = new RulesPropagationManager(engine15,
                "gr.ntua.vision.monitoring.rules.propagation", 10010);

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
        return client.resource(RulesPropagationSimpleTest.ROOT_URL);
    }
}
