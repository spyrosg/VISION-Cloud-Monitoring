package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;


/**
 * The handler that passes events down to a queue.
 */
abstract class CDMIQueueEventHandler implements EventHandler {
    /***/
    private final CDMIQueue q;


    /**
     * Constructor.
     * 
     * @param q
     */
    public CDMIQueueEventHandler(final CDMIQueue q) {
        this.q = q;
    }


    /**
     * Collect a notification.
     * 
     * @param notification
     */
    protected void collect(final MonitoringEvent notification) {
        q.add(notification);
    }
}
