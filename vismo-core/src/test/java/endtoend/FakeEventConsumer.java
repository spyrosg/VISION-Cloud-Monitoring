package endtoend;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.notify.EventHandler;

import java.util.Map;

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
                    FakeEventConsumer.log.debug(getClass().getSimpleName() + ": " + e.get("originating-machine") + " => " + dict);
            } catch (final Throwable x) {
                x.printStackTrace();
            }
        }
    }

    /**
     * 
     */
    static final Logger         log           = LoggerFactory.getLogger(FakeEventConsumer.class);

    /***/
    private static final String SPECIAL_FIELD = "transaction-throughput";

}
