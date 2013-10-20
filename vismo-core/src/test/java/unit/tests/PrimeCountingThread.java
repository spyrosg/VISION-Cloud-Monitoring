package unit.tests;

import java.util.concurrent.CountDownLatch;


/**
 */
class PrimeCountingThread extends Thread {
    /***/
    private final CountDownLatch latch;


    /**
     * @param latch
     */
    public PrimeCountingThread(final CountDownLatch latch) {
        super("prime-counting");
        this.latch = latch;
    }


    /**
     * @see java.lang.Thread#run()
     */
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


    /**
     * @param n
     * @return <code>true</code> if n is prime.
     */
    private static boolean stupidIsPrime(final int n) {
        for (int i = 2; i <= Math.sqrt(n); ++i)
            if (n % i == 0)
                return false;

        return true;
    }
}
