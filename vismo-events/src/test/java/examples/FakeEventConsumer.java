package examples;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventHandler;
import gr.ntua.vision.monitoring.events.EventRegistry;
import gr.ntua.vision.monitoring.events.VismoEventRegistry;

import java.util.logging.Logger;


/**
 *
 */
public class FakeEventConsumer {
    /**
     *
     */
    public static class LoggingHandler implements EventHandler {
        /***/
        private static Logger log = Logger.getLogger(LoggingHandler.class.getName());


        /**
         * @see gr.ntua.vision.monitoring.events.EventHandler#handle(gr.ntua.vision.monitoring.events.Event)
         */
        @Override
        public void handle(final Event e) {
            log.info(String.format("received: timestamp=%s, service=%s, topic=%s, type=%s", e.timestamp(),
                                   e.originatingService(), e.topic(), e.get("type")));
        }
    }


    /**
     * @param args
     */
    public static void main(final String... args) {
        final EventRegistry registry = new VismoEventRegistry(true);

        registry.registerToAll(new LoggingHandler());
    }
}
