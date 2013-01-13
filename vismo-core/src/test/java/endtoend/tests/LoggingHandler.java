package endtoend.tests;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.VismoEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class LoggingHandler implements EventHandler {
    /***/
    private static final Logger log           = LoggerFactory.getLogger(LoggingHandler.class);
    /***/
    private static final String SPECIAL_FIELD = "transaction-throughput";


    /**
     * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void handle(final Event e) {
        try {
            @SuppressWarnings("rawtypes")
            final Map dict = ((VismoEvent) e).dict();
            final Object special = e.get(SPECIAL_FIELD);

            if (special != null)
                log.debug("received event from [" + e.get("originating-machine") + "] => " + dict);
        } catch (final Throwable x) {
            x.printStackTrace();
        }
    }
}
