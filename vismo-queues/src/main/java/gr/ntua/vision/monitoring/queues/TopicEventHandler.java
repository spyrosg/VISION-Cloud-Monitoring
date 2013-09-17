package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 *
 */
public class TopicEventHandler extends CDMIQueueEventHandler {
    /**
     * Constructor.
     * 
     * @param q
     */
    public TopicEventHandler(final CDMIQueue q) {
        super(q);
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void handle(final MonitoringEvent event) {
        collect(event);
    }
}
