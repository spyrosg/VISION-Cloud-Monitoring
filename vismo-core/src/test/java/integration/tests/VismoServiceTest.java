package integration.tests;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.dispatch.VismoEventDispatcher;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.rules.Rule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.service.ClusterHeadNodeFactory;
import gr.ntua.vision.monitoring.service.Service;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;


/**
 * 
 */
public class VismoServiceTest {
    /**
     * 
     */
    private static class EventCountHandler implements EventHandler {
        /***/
        private int counter = 0;


        /**
         * Constructor.
         */
        public EventCountHandler() {
        }


        /**
         * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.Event)
         */
        @Override
        public void handle(final Event e) {
            if (e != null)
                ++counter;
        }


        /**
         * @param noExpectedEvents
         */
        public void hasSeenExpectedNoEvents(final int noExpectedEvents) {
            assertEquals(noExpectedEvents, counter);
        }
    }


    /**
     * This is used to count the number of events received.
     */
    private final class EventCountRule extends Rule {
        /***/
        private int counter = 0;


        /**
         * Constructor.
         * 
         * @param engine
         */
        public EventCountRule(final VismoRulesEngine engine) {
            super(engine);
        }


        /**
         * @param expectedNoEvents
         */
        public void hasSeenExpectedNoEvents(final int expectedNoEvents) {
            assertEquals(expectedNoEvents, counter);
        }


        /**
         * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
         */
        @Override
        public void performWith(final Event e) {
            if (e != null)
                ++counter;
        }
    }
    /***/
    private static final int         NO_GET_OPS    = 10;
    /***/
    private static final int         NO_PUT_OPS    = 10;
    /***/
    @SuppressWarnings("serial")
    private static final Properties  p             = new Properties() {
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
                                                       }
                                                   };
    /***/
    EventCountRule                   countRule;
    /***/
    private final VismoConfiguration conf          = new VismoConfiguration(p);
    /***/
    private final EventCountHandler  countHandler  = new EventCountHandler();
    /***/
    private FakeObjectService        obs;
    /***/
    private Service                  service;
    /** the socket factory. */
    private final ZMQFactory         socketFactory = new ZMQFactory(new ZContext());


    /**
     * @param noOps
     */
    public void doGETs(final int noOps) {
        for (int i = 0; i < noOps; ++i)
            obs.getEvent("ntua", "bill", "foo-container", "bar-object").send();
    }


    /**
     * @param noOps
     */
    public void doPUTs(final int noOps) {
        for (int i = 0; i < noOps; ++i)
            obs.putEvent("ntua", "bill", "foo-container", "bar-object").send();
    }


    /**
     * @throws IOException
     */
    @Before
    public void setUp() throws IOException {
        obs = new FakeObjectService(new VismoEventDispatcher("fake-obs", conf, socketFactory));

        final ClusterHeadNodeFactory serviceFactory = new ClusterHeadNodeFactory(conf, socketFactory) {
            @Override
            protected void boostrap(final VismoRulesEngine engine) {
                super.boostrap(engine);
                countRule = new EventCountRule(engine);
                countRule.submit();
            }
        };

        service = serviceFactory.build(new VismoVMInfo());
    }


    /***/
    @After
    public void tearDown() {
        if (service != null)
            service.halt();

        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        socketFactory.destroy();
    }


    /**
     * @throws InterruptedException
     */
    @Test
    public void vismoDeliversEventsToClient() throws InterruptedException {
        final VismoEventRegistry reg = new VismoEventRegistry(socketFactory, "tcp://127.0.0.1:" + conf.getConsumersPort());

        service.start();
        reg.registerToAll(countHandler);

        doGETs(NO_GET_OPS);
        doPUTs(NO_PUT_OPS);

        waitForEventsDelivery(1000);
        assertThatClientReceivedEvents();
    }


    /**
     * @throws InterruptedException
     */
    @Test
    public void vismoReceivesEventsFromProducers() throws InterruptedException {
        service.start();

        doGETs(NO_GET_OPS);
        doPUTs(NO_PUT_OPS);

        waitForEventsDelivery(1000);
        assertThatVismoReceivedEvents();
    }


    /**
     * 
     */
    private void assertThatClientReceivedEvents() {
        countHandler.hasSeenExpectedNoEvents(NO_GET_OPS + NO_PUT_OPS);
    }


    /***/
    private void assertThatVismoReceivedEvents() {
        countRule.hasSeenExpectedNoEvents(NO_GET_OPS + NO_PUT_OPS);
    }


    /**
     * @param n
     * @throws InterruptedException
     */
    private static void waitForEventsDelivery(final int n) throws InterruptedException {
        Thread.sleep(n);
    }
}
