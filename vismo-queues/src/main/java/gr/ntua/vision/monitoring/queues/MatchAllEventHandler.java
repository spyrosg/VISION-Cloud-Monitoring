package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 *
 */
public class MatchAllEventHandler extends CDMIQueueEventHandler {
    /**
     * Constructor.
     * 
     * @param q
     */
    public MatchAllEventHandler(final CDMINotificationQueue q) {
        super(q);
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void handle(final MonitoringEvent notification) {
        collect(notification);
    }
}
