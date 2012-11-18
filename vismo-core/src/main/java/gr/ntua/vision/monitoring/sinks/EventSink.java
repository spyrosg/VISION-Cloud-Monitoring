package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.events.Event;


/**
 * This is used to transmit events out of the system.
 */
public interface EventSink {
    /**
     * Send the event out.
     * 
     * @param e
     *            the event to transmit.
     */
    void send(Event e);
}
