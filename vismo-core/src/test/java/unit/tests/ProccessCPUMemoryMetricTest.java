package unit.tests;

import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.metrics.ProccessCPUMemoryMetric;
import gr.ntua.vision.monitoring.metrics.ProccessCPUMemoryMetric.CPUMemory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;


/**
 *
 */
public class ProccessCPUMemoryMetricTest extends TestCase {
    /***/
    private final CountDownLatch    latch = new CountDownLatch(1);
    /***/
    private ProccessCPUMemoryMetric usage;
    /***/
    private final VismoVMInfo       vm    = new VismoVMInfo();


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    public void testShouldReportCPUAndMemoryUsage() throws IOException, InterruptedException {
        final int pid = vm.getPID();

        usage = new ProccessCPUMemoryMetric(pid);
        latch.await();

        final CPUMemory cm = usage.get();

        System.err.println("cpu load for this jvm (pid " + pid + ") is about " + cm.cpuLoad + "%, mem usage is about "
                + cm.memoryUsage + " bytes");
        assertTrue("expecting cpu load for jvm to be greater than zero", cm.cpuLoad > 0);
        assertTrue("expecting memory usage for jvm to be greater than zero", cm.memoryUsage > 0);
    }


    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final PrimeCountingThread t1 = new PrimeCountingThread(latch);

        t1.setDaemon(true);
        t1.start();
    }
}
