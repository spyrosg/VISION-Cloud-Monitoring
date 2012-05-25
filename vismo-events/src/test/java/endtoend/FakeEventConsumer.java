package endtoend;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventHandler;
import gr.ntua.vision.monitoring.events.EventRegistry;
import gr.ntua.vision.monitoring.events.VismoEventRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class FakeEventConsumer {
    /**
     *
     */
    public static class LoggingHandler implements EventHandler {
        /***/
        private static Logger log = LoggerFactory.getLogger(LoggingHandler.class);


        /**
         * @see gr.ntua.vision.monitoring.events.EventHandler#handle(gr.ntua.vision.monitoring.events.Event)
         */
        @Override
        public void handle(final Event e) {
            log.info("received: {}", e);
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
