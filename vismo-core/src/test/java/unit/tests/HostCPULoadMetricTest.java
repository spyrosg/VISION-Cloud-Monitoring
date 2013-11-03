package unit.tests;

import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.metrics.HostCPULoadMetric;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;


/**
 *
 */
public class HostCPULoadMetricTest extends TestCase {
    /***/
    private HostCPULoadMetric    cpuLoad;
    /***/
    private final CountDownLatch latch = new CountDownLatch(2);
    /***/
    private final VismoVMInfo    vm    = new VismoVMInfo();


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    public void testShouldGetCPULoad() throws IOException, InterruptedException {
        cpuLoad = new HostCPULoadMetric();

        final double load = cpuLoad.get();

        latch.await();
        System.err.println("cpu load for this host (" + vm.getAddress().getHostAddress() + ") is about " + load);
        assertTrue("expecting cpu load for this host to be greater than zero", load > 0);
    }


    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final PrimeCountingThread t1 = new PrimeCountingThread(latch);
        t1.setDaemon(true);

        final PrimeCountingThread t2 = new PrimeCountingThread(latch);
        t2.setDaemon(true);

        t1.start();
        t2.start();
    }
}
