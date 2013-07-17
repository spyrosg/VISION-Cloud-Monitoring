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
    public ObsEventHandler(final CDMINotificationQueue q, final String op) {
        super(q);
        this.op = op;
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void handle(final MonitoringEvent notification) {
        if (!OBS_SERVICE.equals(notification.originatingService()))
            return;
        if (!op.equals(notification.get(key)))
            return;

        collect(notification);
    }
}
