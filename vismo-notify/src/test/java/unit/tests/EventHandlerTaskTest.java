package unit.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import endtoend.tests.FakeMonitoringInstance;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.EventHandlerTask;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;


/**
 * 
 */
public class EventHandlerTaskTest {
    /**
     * 
     */
    private static class CountingEventHandler implements EventHandler {
        /***/
        final AtomicInteger          noReceivedEvents;
        /***/
        private final CountDownLatch latch;
        /***/
        private final int            noExpectedEvents;


        /**
         * Constructor.
         * 
         * @param latch
         * @param noExpectedEvents
         */
        public CountingEventHandler(final CountDownLatch latch, final int noExpectedEvents) {
            this.latch = latch;
            this.noExpectedEvents = noExpectedEvents;
            this.noReceivedEvents = new AtomicInteger();
        }


        /**
         * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
         */
        @Override
        public void handle(final MonitoringEvent e) {
            if (e == null)
                return;

            if (noReceivedEvents.incrementAndGet() == noExpectedEvents)
                latch.countDown();
        }
    }

    /***/
    private static final String    CONSUMERS_PORT    = "tcp://127.0.0.1:27890";
    /***/
    private static final int       NO_EVENTS_TO_SEND = 100;
    /***/
    private static final String    TOPIC             = "foo";
    /***/
    private CountingEventHandler   handler;
    /***/
    private final CountDownLatch   latch             = new CountDownLatch(1);
    /***/
    private FakeMonitoringInstance mon;
    /***/
    private VismoEventRegistry     registry;
    /***/
    private final ZMQFactory       socketFactory     = new ZMQFactory(new ZContext());
    /***/
    private EventHandlerTask       task;


    /**
     * 
     */
    @Before
    public void setUp() {
        mon = new FakeMonitoringInstance(socketFactory.newPubSocket(CONSUMERS_PORT), NO_EVENTS_TO_SEND, new String[] { TOPIC });
        registry = new VismoEventRegistry(socketFactory, CONSUMERS_PORT);
        handler = new CountingEventHandler(latch, NO_EVENTS_TO_SEND);
        task = registry.register(TOPIC, handler);
    }


    /**
     * @throws InterruptedException
     */
    @Test
    public void taskShouldHaltOnCommand() throws InterruptedException {
        spinThread(30);
        assertTrue("task should've started", task.isRunning());

        mon.sendEvents();
        latch.await(1, TimeUnit.SECONDS);
        task.halt();
        spinThread(30);

        assertFalse("task should've halted", task.isRunning());
    }


    /***/
    @After
    public void tearDown() {
        mon.halt();
    }


    /**
     * @param n
     * @throws InterruptedException
     */
    private static void spinThread(final long n) throws InterruptedException {
        Thread.sleep(n);
    }
}
