package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;


/**
 *
 */
public interface EventSink {
    /**
     * @param e
     */
    void send(Event e);
}
