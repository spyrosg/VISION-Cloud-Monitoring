package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 *
 */
class StortletsEventHandler extends CDMIQueueEventHandler {
    /***/
    private static final String SRE_SERVICE = "SRE";


    /**
     * Constructor.
     * 
     * @param q
     */
    public StortletsEventHandler(final CDMIQueue q) {
        super(q);
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void handle(final MonitoringEvent event) {
        if (SRE_SERVICE.equals(event.originatingService()))
            collect(event);
    }
}
