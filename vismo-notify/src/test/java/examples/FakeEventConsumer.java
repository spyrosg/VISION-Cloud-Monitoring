package examples;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;


/**
 *
 */
public class FakeEventConsumer {
    /**
     *
     */
    public static class LoggingHandler implements EventHandler {
        /**
         * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
         */
        @Override
        public void handle(final MonitoringEvent e) {
            try {
                final Object special = e.get(FakeEventConsumer.SPECIAL_FIELD);

                if (special != null)
                    System.err.println(getClass().getSimpleName() + ": " + e.get("originating-machine") + " => " + e.toString());
            } catch (final Throwable x) {
                x.printStackTrace();
            }
        }
    }

    /***/
    private static final String SPECIAL_FIELD = "transaction-throughput";


    /**
     * @param args
     */
    public static void main(final String... args) {
        final VismoEventRegistry registry = new VismoEventRegistry("tcp://10.0.1.103:56430");

        registry.registerToAll(new LoggingHandler());
    }
}
