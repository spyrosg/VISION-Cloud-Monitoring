package gr.ntua.vision.monitoring.perf;

import gr.ntua.vision.monitoring.dispatch.EventDispatcher;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;


/**
 * 
 */
public class ConstantSizeEventService implements EventService {
    /**
     * 
     */
    private static class ConstantRateTask extends TimerTask {
        /***/
        int                           noSentEvents;
        /***/
        private final EventDispatcher dispatcher;
        /***/
        private final String          dummyValue;
        /***/
        private final CountDownLatch  latch;
        /***/
        private final long            noEvents;
        /***/
        private final String          topic;


        /**
         * Constructor.
         * 
         * @param dispatcher
         * @param latch
         * @param topic
         * @param noEvents
         * @param dummyValue
         */
        public ConstantRateTask(final EventDispatcher dispatcher, final CountDownLatch latch, final String topic,
                final long noEvents, final String dummyValue) {
            this.dispatcher = dispatcher;
            this.latch = latch;
            this.topic = topic;
            this.noEvents = noEvents;
            this.dummyValue = dummyValue;
            this.noSentEvents = 0;
        }


        /**
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {
            for (int i = 0; i < noEvents; ++i) {
                dispatcher.newEvent().field("topic", topic).field("operation", "GET").field("dummy", dummyValue).send();
                ++noSentEvents;
            }

            latch.countDown();
        }
    }

    /***/
    public static final int       JSON_DIFF = 207;
    /** in millis */
    private static final long     PERIOD    = 20;
    /***/
    private final EventDispatcher dispatcher;
    /***/
    private final Timer           timer;


    /**
     * Constructor.
     * 
     * @param dispatcher
     */
    public ConstantSizeEventService(final EventDispatcher dispatcher) {
        this.timer = new Timer(true);
        this.dispatcher = dispatcher;
    }


    /**
     * @see gr.ntua.vision.monitoring.perf.EventService#send(java.lang.String, double, int, long)
     */
    @Override
    public int send(final String topic, final double rate, final int noEvents, final long size) {
        final double executionDuration = (noEvents / rate) * 1000; // in millis
        final double noExecutions = executionDuration / PERIOD;
        final long noEventsPerExecution = (long) Math.ceil(noEvents / noExecutions);
        final CountDownLatch latch = new CountDownLatch((int) Math.ceil(noExecutions));
        final ConstantRateTask task = new ConstantRateTask(dispatcher, latch, topic, noEventsPerExecution, getStringOf(size
                - JSON_DIFF));

        System.out.println("execution duration: " + executionDuration + " msec");
        System.out.println("no executions: " + noExecutions);
        System.out.println("no events per execution: " + noEventsPerExecution);

        timer.schedule(task, 0, PERIOD);

        try {
            latch.await();
        } catch (final InterruptedException ignored) {
            // NOP
        }

        task.cancel();
        System.out.println("no sent events: " + task.noSentEvents);
        return task.noSentEvents;
    }


    /**
     * @param size
     * @return a string of size <code>size</code> in bytes.
     */
    private static String getStringOf(final long size) {
        final StringBuilder buf = new StringBuilder();

        for (int i = 0; i < size; ++i)
            buf.append('a');

        return buf.toString();
    }
}
