package endtoend;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventHandler;

import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;


/**
 *
 */
public class VismoEndToEndTest {
    /**
     *
     */
    public static class LoggingEventHandler implements EventHandler {
        /***/
        private static final Logger log = LoggerFactory.getLogger(LoggingEventHandler.class);


        /**
         * @see gr.ntua.vision.monitoring.events.EventHandler#handler(gr.ntua.vision.monitoring.events.Event)
         */
        @Override
        public void handler(final Event e) {
            log.trace("event: {}", e);
        }
    }

    /** this is the endpoint used to send/receive events. */
    private static final String     EVENTS_END_POINT = "ipc:///tmp/vision.test.events";
    /** the udp port. */
    private static final int        UDP_PORT         = 56431;
    /***/
    @Rule
    public final ExpectedException  thrown           = ExpectedException.none();
    /***/
    private final ZContext          ctx              = new ZContext();
    /***/
    private MonitoringDriver        driver;
    /***/
    private FakeEventConsumer       eventConsumer;
    /***/
    private final FakeEventProducer eventProducer    = new FakeEventProducer(ctx, EVENTS_END_POINT);
    /***/
    private final EventRegister     registry         = new EventRegister(ctx);


    /**
     * @throws Exception
     */
    @Test
    public void monitoringReceivesEventsFromEventProducer() throws Exception {
        driver.start();
        eventProducer.start();
        eventConsumer.start();
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
        driver = new MonitoringDriver();
        driver.addUDPServer(UDP_PORT);
        driver.addEventReceiver(ctx, EVENTS_END_POINT);

        eventConsumer = new FakeEventConsumer(registry);
    }


    /***/
    @After
    public void tearDown() {
        eventProducer.stop();
    }


    /**
     * @throws InterruptedException
     */
    private static void giveEnoughTimeToReceiveEvents() throws InterruptedException {
        Thread.sleep(500);
    }
}
