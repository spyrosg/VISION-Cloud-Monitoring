package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 * This is used to transmit events out of the system.
 */
public interface EventSink {
    /**
     * 
     */
    void close();


    /**
     * Send the event out.
     * 
     * @param e
     *            the event to transmit.
     */
    void send(MonitoringEvent e);
}
