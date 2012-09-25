package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.sources.EventSource;


/**
 * 
 */
public interface AggregationWorker extends EventListener {
    /**
     * @param e
     * @return
     */
    Event perform(final Event e);


    /**
     * @param source
     */
    void subscribeWith(final EventSource source);
}
