package endtoend;

import org.junit.After;
import org.junit.Test;


/**
 *
 */
public class VismoEndToEndTest {
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
        monitoring.leaveCluster();
    }


    /**
     * 
     */
    @After
    public void tearDown() {
        cluster.shutDown();
    }
}
