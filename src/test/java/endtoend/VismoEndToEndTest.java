package endtoend;

import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zeromq.ZContext;


/**
 *
 */
public class VismoEndToEndTest {
    /** this is the endpoint used to send/receive events. */
    private static final String     EVENTS_END_POINT = "ipc:///tmp/vision.events";
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
    private final FakeEventProducer eventProducer    = new FakeEventProducer(ctx, EVENTS_END_POINT);


    /**
     * @throws Exception
     */
    @Test
    public void monitoringReceivesEventsFromEventProducer() throws Exception {
        driver.start();
        eventProducer.start();
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
    }


    /**
     * 
     */
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
