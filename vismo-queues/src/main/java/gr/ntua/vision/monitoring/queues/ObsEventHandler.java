package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 *
 */
class ObsEventHandler extends CDMIQueueEventHandler {
    /***/
    private static final String key         = "operation";
    /***/
    private static final String OBS_SERVICE = "object_service";
    /***/
    private final String        op;


    /**
     * Constructor.
     * 
     * @param q
     * @param op
     */
    public ObsEventHandler(final CDMIQueue q, final String op) {
        super(q);
        this.op = op;
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void handle(final MonitoringEvent event) {
        if (!OBS_SERVICE.equals(event.originatingService()))
            return;
        if (!op.equals(event.get(key)))
            return;
        if (event.topic() != null)
            return;

        collect(event);
    }
}
