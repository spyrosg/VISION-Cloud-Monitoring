package gr.ntua.vision.monitoring.notify;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 * This interface is expected to be implemented by the various <em>Vismo</em> consumers. The {@link EventRegistry} is responsible
 * to notify the handler upon event receipt. Each event handler is expected to handle events of just one topic.
 */
public interface EventHandler {
    /**
     * Process, or otherwise handle the event.
     * 
     * @param e
     *            the event received.
     */
    void handle(MonitoringEvent e);
}
