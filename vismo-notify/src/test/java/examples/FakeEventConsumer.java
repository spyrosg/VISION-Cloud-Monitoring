package examples;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.EventRegistry;
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
         * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.Event)
         */
        @Override
        public void handle(final Event e) {
            System.out.println(String
                    .format("received: timestamp=%s, service=%s, topic=%s, tenant=%s, user=%s, container=%s, object=%s",
                            e.timestamp(), e.originatingService(), e.topic(), e.get("tenant"), e.get("user"), e.get("container"),
                            e.get("object")));
        }
    }


    /**
     * @param args
     */
    public static void main(final String... args) {
        final EventRegistry registry = new VismoEventRegistry();

        registry.registerToAll(new LoggingHandler());
    }
}
