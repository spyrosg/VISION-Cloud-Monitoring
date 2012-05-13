package endtoend;

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
    /** the udp port. */
    private final int              UDP_SERVER_PORT = 56431;
    /***/
    private final MonitoringDriver monitoring      = new MonitoringDriver(UDP_SERVER_PORT);


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

        thrown.expect(AssertionError.class);
        monitoring.reportsStatus();
    }
}
