package endtoend;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 */
public class VismoEndToEndTest {
    /***/
    @Rule
    public final ExpectedException  thrown     = ExpectedException.none();
    /***/
    private final FakeVisionCluster cluster    = new FakeVisionCluster();
    /***/
    private final MonitoringDriver  monitoring = new MonitoringDriver();


    /**
     * @throws Exception
     */
    @Test
    public void monitoringConnectsAndSendsMessagesToCluster() throws Exception {
        cluster.start();
        monitoring.start();
        cluster.receivesNewJoin();
        monitoring.reportsStatus();
        cluster.receivesEvents();
        monitoring.reportsStatus();
        monitoring.shutdown();

        thrown.expect(AssertionError.class);
        monitoring.reportsStatus();
    }


    /***/
    @After
    public void tearDown() {
        cluster.shutDown();
    }
}
