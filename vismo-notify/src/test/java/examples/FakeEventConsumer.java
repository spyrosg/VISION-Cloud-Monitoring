package examples;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.EventRegistry;

import org.zeromq.ZContext;


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
                    .format("received: timestamp=%s, service=%s, topic=%s, type=%s, tenant=%s, user=%s, container=%s, object=%s",
                            e.timestamp(), e.originatingService(), e.topic(), e.get("type"), e.get("tenant"), e.get("user"),
                            e.get("container"), e.get("obj")));
        }
    }


    /**
     * @param args
     */
    public static void main(final String... args) {
        final EventRegistry registry = new EventRegistry(new ZContext(), "tcp://10.0.1.214:27890");

        registry.registerToAll(new LoggingHandler());
    }
}
