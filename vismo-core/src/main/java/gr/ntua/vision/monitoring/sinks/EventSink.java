package gr.ntua.vision.monitoring.sinks;

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
