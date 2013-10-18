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
    final CountDownLatch      latch = new CountDownLatch(1);
    /***/
    private ProccessCPULoad   cpuLoad;
    /***/
    private final VismoVMInfo vm    = new VismoVMInfo();


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    public void testShouldGetCPULoad() throws IOException, InterruptedException {
        final int pid = vm.getPID();

        cpuLoad = new ProccessCPULoad(pid);
        latch.await();
        System.err.println("cpu load for this jvm (pid " + pid + ") is about " + cpuLoad.get());
        assertTrue("expecting cpu load for jvm to be greater than zero", cpuLoad.get() > 0);
    }


    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        startPrimeCountingThread();
    }


    /**
     * 
     */
    private void startPrimeCountingThread() {
        final Thread t = new Thread() {
            @Override
            public void run() {
                final long start = System.nanoTime();
                int counter = 0;

                for (int n = 3; n < Integer.MAX_VALUE; n += 2) {
                    if (stupidIsPrime(n))
                        ++counter;
                    if (System.nanoTime() - start >= 2e9)
                        latch.countDown();
                    if (System.nanoTime() - start >= 3e9) {
                        System.err.println("there are " + counter + " primes under " + n);
                        break;
                    }
                }
            }


            private boolean stupidIsPrime(final int n) {
                for (int i = 2; i <= Math.sqrt(n); ++i)
                    if (n % i == 0)
                        return false;

                return true;
            }
        };

        t.setDaemon(true);
        t.start();
    }
}
