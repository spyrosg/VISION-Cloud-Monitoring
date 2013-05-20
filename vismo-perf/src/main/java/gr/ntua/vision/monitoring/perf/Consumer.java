package gr.ntua.vision.monitoring.perf;

import static gr.ntua.vision.monitoring.perf.Utils.requireFile;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.EventHandlerTask;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * 
 */
public class Consumer {
    /**
     * 
     */
    private static class PerfHandler implements EventHandler {
        /***/
        int                          noReceivedEvents;
        /***/
        private final int            eventSize;
        /***/
        private final CountDownLatch latch;


        /**
         * Constructor.
         * 
         * @param latch
         * @param eventSize
         */
        public PerfHandler(final CountDownLatch latch, final int eventSize) {
            this.latch = latch;
            this.eventSize = eventSize;
            this.noReceivedEvents = 0;
        }


        /**
         * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
         */
        @Override
        public void handle(final MonitoringEvent e) {
            if (e == null)
                return;

            ++noReceivedEvents;
            latch.countDown();

            final long now = System.currentTimeMillis();
            final double lat = getLatency(now, e);
            final double throughput = eventSize / lat;

            System.out.println(e.timestamp() + "," + lat + "," + eventSize + "," + throughput);
        }


        /**
         * @param now
         * @param e
         * @return d
         */
        private static double getLatency(final long now, final MonitoringEvent e) {
            return (now - e.timestamp()) / 1000.0;
        }
    }

    /***/
    private static final String PROG = "Consumer";


    /**
     * @param args
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        if (args.length < 3) {
            System.err.println("arg count");
            System.err.println(PROG + " config-file topic event-size no-events");
            System.exit(1);
        }

        final String configFile = args[0];
        final String topic = args[1];
        final int eventSize = Integer.valueOf(args[2]);
        final int noEvents = Integer.valueOf(args[3]);

        requireFile(configFile);

        final VismoConfiguration conf = new VismoConfiguration(configFile);
        final String consumersAddress = "tcp://" + conf.getClusterHead() + ":" + conf.getConsumersPort();
        final VismoEventRegistry registry = new VismoEventRegistry(consumersAddress);
        final CountDownLatch latch = new CountDownLatch(noEvents);
        final PerfHandler handler = new PerfHandler(latch, eventSize);
        final EventHandlerTask task = registry.register(topic, handler);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            /**
             * @see java.lang.Thread#run()
             */
            @Override
            public void run() {
                System.err.println("# no received events: " + handler.noReceivedEvents);
            }
        });

        System.out.println("# timestamp, latency, bytes, throughtput");
        System.err.println("# timestamp, latency, bytes, throughtput");
        latch.await(5, TimeUnit.MINUTES);
        task.halt();
        registry.halt();
    }
}
