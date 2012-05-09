package endtoend;

import org.junit.After;
import org.junit.Test;


/**
 *
 */
public class VismoEndToEndTest {
    /***/
    private final VisionCloudCluster          cluster    = new VisionCloudCluster();
    /***/
    private final MonitoringDriver monitoring = new MonitoringDriver();


    /**
     * @throws Exception
     */
    @Test
    public void monitoringSendsMessagesAcrossTheCluster() throws Exception {
        cluster.start();
        monitoring.start();
        cluster.receivesNewJoin();
        monitoring.reportsStatus();
        cluster.receivesEvents();
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
