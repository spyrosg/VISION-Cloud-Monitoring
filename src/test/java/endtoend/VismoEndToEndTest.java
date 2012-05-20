package endtoend;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventHandler;
import gr.ntua.vision.monitoring.events.EventRegistry;

import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;


/**
 *
 */
public class VismoEndToEndTest {
    private static final String     EVENTS_DISTRIBUTION_PORT = "tcp://127.0.0.1:34890";
    private static final String     LOCAL_EVENTS_ENTRY_PORT  = "ipc:///tmp/vision.test.events";
    /** the maximum number of events to sent for the test. */
    private static final int        NO_EVENTS_TO_SENT        = 10;
    /** the udp port. */
    private static final int        UDP_PORT                 = 56431;
    /***/
    private final ZContext          ctx                      = new ZContext();
    /***/
    private final MonitoringDriver  driver                   = new MonitoringDriver();
    /***/
    private FakeEventConsumer       eventConsumer;
    /***/
    private final EventCountHandler eventConsumerCounter     = new EventCountHandler(NO_EVENTS_TO_SENT);
    /***/
    private final FakeEventProducer eventProducer            = new FakeEventProducer(ctx, LOCAL_EVENTS_ENTRY_PORT,
                                                                     NO_EVENTS_TO_SENT);
    /***/
    private final EventRegistry     registry                 = new EventRegistry(ctx, EVENTS_DISTRIBUTION_PORT);


    /**
     * @throws Exception
     */
    @Test
    public void monitoringReceivesEventsFromEventProducer() throws Exception {
        driver.start();
        driver.reportsMonitoringStatus(UDP_PORT);
        Thread.sleep(1000);
        eventProducer.sendEvents();
        giveEnoughTimeToReceiveEvents();
        driver.reportsMonitoringStatus(UDP_PORT);
        driver.shutdown();
    }


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        driver.setup(UDP_PORT, ctx, LOCAL_EVENTS_ENTRY_PORT, EVENTS_DISTRIBUTION_PORT);
        setupConsumer();
        eventProducer.start();
        registry.start();
    }


    /***/
    @After
    public void tearDown() {
        eventConsumerCounter.haveReceivedEnoughMessages();
        eventProducer.stop();
    }


    /**
     * 
     */
    private void setupConsumer() {
        eventConsumer = new FakeEventConsumer(registry);
        eventConsumer.registerToAll(new EventHandler() {
            private final Logger log = LoggerFactory.getLogger(getClass());


            @Override
            public void handler(final Event e) {
                log.trace("eventConsumer.received: {}", e);
            }
        });
        eventConsumer.registerToAll(eventConsumerCounter);
    }


    /**
     * @throws InterruptedException
     */
    private static void giveEnoughTimeToReceiveEvents() throws InterruptedException {
        Thread.sleep(1000);
    }
}
