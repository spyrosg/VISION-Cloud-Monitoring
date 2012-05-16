package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to log the incoming events.
 */
public class LogEventListener implements EventListener {
    /** the log target. */
    private final static Logger log = LoggerFactory.getLogger(LogEventListener.class);


    /**
     * @see gr.ntua.vision.monitoring.events.EventListener#notify(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void notify(final Event e) {
        log.debug("receiving event {}", e);
    }
}
