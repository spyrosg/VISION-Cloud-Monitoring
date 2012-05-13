package endtoend;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 */
public class VismoEndToEndTest {
    /***/
    @Rule
    public final ExpectedException thrown          = ExpectedException.none();
    /***/
    private MonitoringDriver       monitoring;
    /** the udp port. */
    private final int              UDP_SERVER_PORT = 56431;


    /**
     * @throws Exception
     */
    @Test
    public void monitoringStartsAndStopsPromptly() throws Exception {
        monitoring.start();
        monitoring.reportsStatus();
        Thread.sleep(1000);
        monitoring.reportsStatus();
        monitoring.shutdown();
    }


    /**
     * 
     */
    @Before
    public void setUp() {
        monitoring = new MonitoringDriver(UDP_SERVER_PORT);
    }
}
