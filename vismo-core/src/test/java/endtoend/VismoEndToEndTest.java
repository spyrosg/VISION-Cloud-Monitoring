package endtoend;

import gr.ntua.vision.monitoring.events.EventRegistry;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;


/**
 *
 */
public class VismoEndToEndTest {
    private static final String     EVENTS_DISTRIBUTION_PORT = "tcp://127.0.0.1:34890";
    private static final String     LOCAL_EVENTS_ENTRY_PORT;
    /** the maximum number of events to sent for the test. */
    private static final int        NO_EVENTS_TO_SENT        = 10;
    /***/
    private static final File       tmp;
    /** the udp port. */
    private static final int        UDP_PORT                 = 56431;
    /***/
    private final ZContext          ctx                      = new ZContext();
    /***/
    private MonitoringDriver        driver;
    /***/
    private final EventCountHandler eventConsumerCounter     = new EventCountHandler(NO_EVENTS_TO_SENT);
    /***/
    private final FakeEventProducer eventProducer            = new FakeEventProducer(ctx, LOCAL_EVENTS_ENTRY_PORT,
                                                                     NO_EVENTS_TO_SENT);
    /***/
    private final EventRegistry     registry                 = new EventRegistry(ctx, EVENTS_DISTRIBUTION_PORT);

    static {
        try {
            tmp = File.createTempFile("vismo.", ".ports", new File(System.getProperty("java.io.tmpdir")));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        LOCAL_EVENTS_ENTRY_PORT = "ipc://" + tmp;
    }


    /**
     * @throws Exception
     */
    @Test
    public void monitoringReceivesEventsFromEventProducer() throws Exception {
        driver.start();
        driver.reportsMonitoringStatus(UDP_PORT);
        eventProducer.sendEvents();
        waitForAllEventsToBeReceived();
        driver.reportsMonitoringStatus(UDP_PORT);
        driver.shutdown();
    }


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        driver = new MonitoringDriver();
        driver.setup(UDP_PORT, ctx, LOCAL_EVENTS_ENTRY_PORT, EVENTS_DISTRIBUTION_PORT);
        eventProducer.start();
        setupConsumer();
    }


    /***/
    @After
    public void tearDown() {
        eventConsumerCounter.haveReceivedEnoughMessages();
        eventProducer.stop();
    }


    /***/
    private void setupConsumer() {
        registry.registerToAll(eventConsumerCounter);
    }


    /**
     * @throws InterruptedException
     */
    private static void waitForAllEventsToBeReceived() throws InterruptedException {
        Thread.sleep(1100);
    }
}
