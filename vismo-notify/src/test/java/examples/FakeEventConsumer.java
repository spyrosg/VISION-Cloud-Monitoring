package examples;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.EventRegistry;

import java.util.Map;


/**
 *
 */
public class FakeEventConsumer {
    /**
     *
     */
    public static class LoggingHandler implements EventHandler {
        /**
         * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.Event)
         */
        @Override
        public void handle(final Event e) {
            try {
                @SuppressWarnings("rawtypes")
                final Map dict = (Map) e.get("!dict");
                final Object special = e.get(FakeEventConsumer.SPECIAL_FIELD);

                if (special != null)
                    System.err.println(getClass().getSimpleName() + ": " + e.get("originating-machine") + " => " + dict);
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
        final EventRegistry registry = new EventRegistry("tcp://10.0.1.103:56430");
        registry.registerToAll(new LoggingHandler());
    }
}
