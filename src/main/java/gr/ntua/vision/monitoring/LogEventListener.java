package gr.ntua.vision.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class LogEventListener implements EventListener {
    /***/
    private final static Logger log = LoggerFactory.getLogger(LogEventListener.class);


    /**
     * 
     */
    public LogEventListener() {
    }


    /**
     * @see gr.ntua.vision.monitoring.EventListener#notify(gr.ntua.vision.monitoring.Event)
     */
    @Override
    public void notify(final Event e) {
        log.debug("received event: {}", e);
    }
}
