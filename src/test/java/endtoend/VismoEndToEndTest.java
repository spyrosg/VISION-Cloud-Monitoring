package endtoend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZMQ;


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
    @Before
    public void setUp() {
        System.out.println(ZMQ.getVersionString());
    }


    /**
     * 
     */
    @After
    public void tearDown() {
        cluster.shutDown();
    }
}
