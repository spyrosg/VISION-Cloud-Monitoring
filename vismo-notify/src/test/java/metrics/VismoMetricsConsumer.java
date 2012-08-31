package metrics;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.EventRegistry;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;


/**
 * 
 */
public class VismoMetricsConsumer {
    /**
     * 
     */
    private static class VismoMetricsEventHandler implements EventHandler {
        /***/
        private static final String END_MESSAGE        = "END";
        /***/
        private static final String START_MESSAGE      = "START";
        /***/
        private long                endTime;
        /***/
        private long                noOfEventsReceived = 0;
        /***/
        private long                startTime;


        /**
         * Constructor.
         */
        public VismoMetricsEventHandler() {
        }


        /**
         * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.Event)
         */
        @Override
        public void handle(final Event e) {
            final String hint = (String) e.get("hint");

            if (hint != null && hint.equals(START_MESSAGE)) {
                this.startTime = System.currentTimeMillis();
                return;
            } else if (hint != null && hint.equals(END_MESSAGE)) {
                this.endTime = System.currentTimeMillis();
                printResults();
                return;
            } else
                ++noOfEventsReceived;
        }


        /**
         * @return
         */
        private double getDuration() {
            return ((endTime - startTime) / 1000.0);
        }


        /**
         * 
         */
        private void printResults() {
            System.out.println("Reporting by " + VismoMetricsConsumer.class);
            System.out.println();
            System.out.println("No of events: " + noOfEventsReceived + " in " + getDuration() + " seconds");
            System.out.println("Throughput " + (1.0 * noOfEventsReceived / getDuration()) + " messages / second");
        }
    }


    /**
     * @param args
     */
    public static void main(final String... args) {
        final EventRegistry registry = new VismoEventRegistry(false);

        registry.registerToAll(new VismoMetricsEventHandler());
    }
}
