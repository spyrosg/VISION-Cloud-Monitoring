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

                System.out.println("=> " + dict);
            } catch (final Throwable x) {
                x.printStackTrace();
            }
        }
    }


    /**
     * @param args
     */
    public static void main(final String... args) {
        final EventRegistry registry = new EventRegistry("tcp://10.0.1.103:56430");

        registry.register("sla-per-request", new LoggingHandler());
    }
}
