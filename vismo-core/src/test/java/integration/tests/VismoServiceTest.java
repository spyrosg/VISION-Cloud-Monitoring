package integration.tests;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.dispatch.VismoEventDispatcher;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.rules.Rule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.service.ClusterHeadNodeFactory;
import gr.ntua.vision.monitoring.service.Service;
import gr.ntua.vision.monitoring.service.VismoService;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;


/**
 * This is used to test the general facilities of the {@link VismoService}; thus is should receive events from producers, process
 * them in a rules engine and dispatch them to consumers.
 */
public class VismoServiceTest {
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
        public void performWith(final MonitoringEvent e) {
            if (e != null)
                ++counter;
        }
    }
    /** the log target. */
    private static final Logger      log           = LoggerFactory.getLogger(VismoServiceTest.class);
    /***/
    private static final int         NO_GET_OPS    = 100;
    /***/
    private static final int         NO_PUT_OPS    = 100;
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

                                                           setProperty("mon.group.addr", "228.5.6.7");
                                                           setProperty("mon.group.port", "12345");
                                                           setProperty("mon.ping.period", "60000");
                                                           setProperty("startup.rules", "PassThroughRule");
                                                           setProperty("web.port", "9996");
                                                       }
                                                   };
    /***/
    EventCountRule                   countRule;
    /***/
    private final VismoConfiguration conf          = new VismoConfiguration(p);
    /***/
    private FakeObjectService        obs;
    /** the object under test. */
    private Service                  service;
    /** the socket factory. */
    private final ZMQFactory         socketFactory = new ZMQFactory(new ZContext());


    /**
     * @throws IOException
     */
    @Before
    public void setUp() throws IOException {
        obs = new FakeObjectService(new VismoEventDispatcher(socketFactory, conf, "fake-obs"));
        service = new ClusterHeadNodeFactory(conf, socketFactory) {
            @Override
            protected void submitRules(final VismoRulesEngine engine) {
                countRule = new EventCountRule(engine);
                countRule.submit();
                super.submitRules(engine);
            }
        }.build(new VismoVMInfo());
    }


    /***/
    @After
    public void tearDown() {
        if (service != null)
            service.halt();
    }


    /**
     * @throws InterruptedException
     */
    @Test
    public void vismoDeliversEventsToClient() throws InterruptedException {
        final VismoEventRegistry reg = new VismoEventRegistry(socketFactory, "tcp://127.0.0.1:" + conf.getConsumersPort());
        final CountDownLatch latch = new CountDownLatch(1);
        final ConsumerHandler consumer = new ConsumerHandler(latch, NO_GET_OPS + NO_PUT_OPS);

        service.start();
        reg.registerToAll(consumer);

        final long start = System.currentTimeMillis();

        doGETs(NO_GET_OPS);
        doPUTs(NO_PUT_OPS);
        log.debug("waiting event delivery...");
        latch.await(10, TimeUnit.SECONDS);

        final double dur = (System.currentTimeMillis() - start) / 1000.0;

        log.debug("{} events delivered to client in {} sec ({} events/sec)", new Object[] { consumer.getNoReceivedEvents(), dur,
                consumer.getNoReceivedEvents() / dur });

        consumerHasReceivedExpectedNoEvents(consumer, NO_GET_OPS + NO_PUT_OPS);
    }


    /**
     * @throws InterruptedException
     */
    @Test
    public void vismoReceivesEventsFromProducers() throws InterruptedException {
        service.start();

        doGETs(NO_GET_OPS);
        doPUTs(NO_PUT_OPS);

        waitForEventsDelivery(2000);
        assertThatVismoReceivedEvents();
    }


    /***/
    private void assertThatVismoReceivedEvents() {
        countRule.hasSeenExpectedNoEvents(NO_GET_OPS + NO_PUT_OPS);
    }


    /**
     * @param noOps
     */
    private void doGETs(final int noOps) {
        for (int i = 0; i < noOps; ++i)
            obs.getEvent("ntua", "bill", "foo-container", "bar-object").send();
    }


    /**
     * @param noOps
     */
    private void doPUTs(final int noOps) {
        for (int i = 0; i < noOps; ++i)
            obs.putEvent("ntua", "bill", "foo-container", "bar-object").send();
    }


    /**
     * @param consumerHandler
     * @param expectedNoEvents
     */
    private static void consumerHasReceivedExpectedNoEvents(final ConsumerHandler consumerHandler, final int expectedNoEvents) {
        assertEquals(expectedNoEvents, consumerHandler.getNoReceivedEvents());
    }


    /**
     * @param n
     * @throws InterruptedException
     */
    private static void waitForEventsDelivery(final int n) throws InterruptedException {
        Thread.sleep(n);
    }
}
