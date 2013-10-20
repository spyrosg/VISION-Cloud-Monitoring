package unit.tests;

import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.metrics.ProccessCPULoad;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;


/**
 *
 */
public class ProccessCPULoadTest extends TestCase {
    /***/
    private ProccessCPULoad      cpuLoad;
    /***/
    private final CountDownLatch latch = new CountDownLatch(1);
    /***/
    private final VismoVMInfo    vm    = new VismoVMInfo();


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    public void testShouldGetCPULoad() throws IOException, InterruptedException {
        final int pid = vm.getPID();

        cpuLoad = new ProccessCPULoad(pid);
        latch.await();

        final double load = cpuLoad.get();

        System.err.println("cpu load for this jvm (pid " + pid + ") is about " + load + "%");
        assertTrue("expecting cpu load for jvm to be greater than zero", load > 0);
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
