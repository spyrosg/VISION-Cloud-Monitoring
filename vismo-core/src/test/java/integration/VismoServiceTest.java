package integration;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoEventDispatcher;
import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.rules.Rule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.service.Service;
import gr.ntua.vision.monitoring.service.WorkerNodeFactory;
import gr.ntua.vision.monitoring.zmq.VismoSocket;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.zeromq.ZContext;


/**
 * 
 */
public class VismoServiceTest {
    /**
     * 
     */
    private final class EventCounter extends Rule {
        /***/
        private int counter = 0;


        /**
         * Constructor.
         * 
         * @param engine
         */
        public EventCounter(final VismoRulesEngine engine) {
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
            System.err.println(this + "#performWith: " + e);

            if (e != null)
                ++counter;
        }
    }

    /***/
    private static final int   NO_READ_EVENTS_TO_SEND  = 10;
    /***/
    private static final int   NO_WRITE_EVENTS_TO_SEND = 10;
    /***/
    EventCounter               counterRule;
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
    private VismoEventRegistry reg;
    /***/
    private Service            service;
    /***/
    private WorkerNodeFactory  serviceFactory;
    /***/
    private final VismoVMInfo  vminfo                  = new VismoVMInfo();
    /***/
    private final ZMQSockets   zmq                     = new ZMQSockets(new ZContext());


    /**
     * @throws InterruptedException
     */
    @Ignore("work in progress")
    @Test
    public void receivesExpectedEvents() throws InterruptedException {
        service.start();
        obs.start();

        obs.sendReadEvents(NO_READ_EVENTS_TO_SEND);
        obs.sendWriteEvents(NO_WRITE_EVENTS_TO_SEND);

        Thread.sleep(2000);
        assertThatServiceReceivedAllEvents(NO_READ_EVENTS_TO_SEND + NO_WRITE_EVENTS_TO_SEND);
    }


    /***/
    @Before
    public void setUp() {
        conf = new VismoConfiguration(p);

        serviceFactory = new WorkerNodeFactory(conf, zmq) {
            @Override
            protected void boostrap(final VismoRulesEngine engine) {
                super.boostrap(engine);
                counterRule = new EventCounter(engine);
                counterRule.submit();
            }
        };

        final VismoSocket sock = zmq.newConnectedPushSocket(conf.getProducersPoint());
        obs = new FakeObjectService(new VismoEventDispatcher("fake-obs", sock));
        System.err.println("vismo dispatcher using: " + sock);
        service = serviceFactory.build(vminfo);
    }


    /**
     * @param noExpectedEvents
     */
    private void assertThatServiceReceivedAllEvents(final int noExpectedEvents) {
        counterRule.hasSeenExpectedNoEvents(noExpectedEvents);
    }
}
