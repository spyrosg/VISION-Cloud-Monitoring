package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.sources.EventSource;


/**
 * A client wishing to receive events from {@link EventSource}s should implement this interface.
 */
public interface EventListener {
    /**
     * Notify the listener of the event received by the source.
     * 
     * @param e
     *            the event received.
     */
    void receive(Event e);
}
