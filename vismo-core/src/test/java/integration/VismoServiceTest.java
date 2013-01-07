package integration;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.dispatch.VismoEventDispatcher;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.rules.Rule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.service.Service;
import gr.ntua.vision.monitoring.service.WorkerNodeFactory;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;


/**
 * 
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
        public void performWith(final Event e) {
            if (e != null)
                ++counter;
        }
    }

    /***/
    private static final int   NO_READ_EVENTS_TO_SEND  = 10;
    /***/
    private static final int   NO_WRITE_EVENTS_TO_SEND = 10;
    /***/
    EventCountRule             countRule;
    /***/
    private VismoConfiguration conf;
    /***/
    private FakeObjectService  obs;
    /***/
    @SuppressWarnings("serial")
    private final Properties   p                       = new Properties() {
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
    private Service            service;
    /***/
    private WorkerNodeFactory  serviceFactory;
    /***/
    private final VismoVMInfo  vminfo                  = new VismoVMInfo();
    /***/
    private final ZMQSockets   zmq                     = new ZMQSockets(new ZContext());


    /***/
    @Before
    public void setUp() {
        obs = new FakeObjectService(new VismoEventDispatcher("fake-obs", conf, zmq));

        serviceFactory = new WorkerNodeFactory(conf, zmq) {
            @Override
            protected void boostrap(final VismoRulesEngine engine) {
                super.boostrap(engine);
                countRule = new EventCountRule(engine);
                countRule.submit();
            }
        };

        obs = new FakeObjectService(new VismoEventDispatcher("fake-obs", zmq.newConnectedPushSocket(conf.getProducersPoint())));
        service = serviceFactory.build(vminfo);
    }


    /**
     * @throws InterruptedException
     */
    @Test
    public void vismoReceivesEventsFromProducers() throws InterruptedException {
        service.start();

        obs.sendReadEvents(NO_READ_EVENTS_TO_SEND);
        obs.sendWriteEvents(NO_WRITE_EVENTS_TO_SEND);

        waitForEventsDelivery(1000);
        assertThatServiceReceivedEvents();
    }


    /***/
    private void assertThatServiceReceivedEvents() {
        countRule.hasSeenExpectedNoEvents(NO_READ_EVENTS_TO_SEND + NO_WRITE_EVENTS_TO_SEND);
    }


    /**
     * @param n
     * @throws InterruptedException
     */
    private static void waitForEventsDelivery(final int n) throws InterruptedException {
        Thread.sleep(n);
    }
}
