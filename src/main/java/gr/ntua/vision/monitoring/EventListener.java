package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;


/**
 * An event listener is used to pass around events to interested parties.
 */
public interface EventListener {
    /**
     * Notify the listener of the event.
     * 
     * @param e
     *            the event received.
     */
    void notify(Event e);
}
