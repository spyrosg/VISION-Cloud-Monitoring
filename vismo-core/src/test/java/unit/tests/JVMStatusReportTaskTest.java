package unit.tests;

import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.threading.JVMStatusReportTask;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;


/**
 * 
 */
public class JVMStatusReportTaskTest {
    /***/
    private final int                 nCPUs = Runtime.getRuntime().availableProcessors();
    /***/
    private final JVMStatusReportTask task  = new JVMStatusReportTask(100);


    /**
     * @throws InterruptedException
     */
    @Test
    public void taskShouldReportSensibleCPUUsage() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(nCPUs);

        for (int i = 0; i < nCPUs; ++i)
            calculatingPrimesThread(latch).start();

        latch.await();
        task.run();

        assertTrue("cpu load should be at least 5%", JVMStatusReportTask.getCPULoad() >= 0.05);
    }


    /**
     * @param latch
     * @return a daemon thread calculating a some prime numbers.
     */
    private Thread calculatingPrimesThread(final CountDownLatch latch) {
        final Thread t = new Thread(getPrimeNumbersRunnable(latch));

        t.setDaemon(true);

        return t;
    }


    /**
     * @param latch
     * @return a runnable.
     */
    private Runnable getPrimeNumbersRunnable(final CountDownLatch latch) {
        return new Runnable() {
            @Override
            public void run() {
                final int NO_PRIMES = 5000000;
                final int[] primes = new int[100];

                for (int i = 2; i <= NO_PRIMES; ++i)
                    if (isPrime(i))
                        primes[i % primes.length] = i;

                latch.countDown();
            }


            /**
             * @param n
             * @return <code>true</code> if the given integer is a prime, <code>false</code> otherwise.
             */
            private boolean isPrime(final int n) {
                // 2 is the smallest prime
                if (n <= 2)
                    return n == 2;

                // even numbers other than 2 are not prime
                if (n % 2 == 0)
                    return false;

                // check odd divisors from 3
                // to the square root of n
                for (int i = 3, end = (int) Math.sqrt(n); i <= end; i += 2)
                    if (n % i == 0)
                        return false;

                return true;
            }
        };
    }
}
