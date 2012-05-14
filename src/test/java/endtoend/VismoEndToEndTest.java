package endtoend;

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
    private static final String      EVENTS_END_POINT = "tcp://127.0.0.1:67891";
    /** the udp port. */
    private static final int         UDP_SERVER_PORT  = 56431;
    /***/
    @Rule
    public final ExpectedException   thrown           = ExpectedException.none();
    /***/
    private final ZContext           ctx              = new ZContext();
    /***/
    private MonitoringDriver         monitoring;
    /***/
    private FakeEventGeneratorServer server;


    /**
     * @throws Exception
     */
    @Test
    public void monitoringStartsAndStopsPromptly() throws Exception {
        monitoring.start();
        server.start();
        monitoring.reportsStatus();
        Thread.sleep(1000);
        server.sendEvents();
        giveEnoughTimeToReceiveEvents();
        monitoring.reportsStatus();
        monitoring.shutdown();
    }


    /**
     * 
     */
    @Before
    public void setUp() {
        server = new FakeEventGeneratorServer(ctx, EVENTS_END_POINT);
        monitoring = new MonitoringDriver(ctx, UDP_SERVER_PORT, EVENTS_END_POINT);
    }


    /**
     * 
     */
    @After
    public void tearDown() {
        server.stop();
    }


    /**
     * @throws InterruptedException
     */
    private static void giveEnoughTimeToReceiveEvents() throws InterruptedException {
        Thread.sleep(500);
    }
}
