package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;

/**
 * The handler that passes events down to a queue.
 */
class TopicQueueHandler implements EventHandler {
    /***/
    private final CDMINotificationQueue q;


    /**
     * Constructor.
     * 
     * @param q
     */
    public TopicQueueHandler(final CDMINotificationQueue q) {
        this.q = q;
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void handle(final MonitoringEvent e) {
        q.add(e);
    }
}