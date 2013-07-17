package gr.ntua.vision.monitoring.queues;

/**
 *
 */
public class ObsPUTEventHandler extends ObsEventHandler {
    /**
     * Constructor.
     * 
     * @param q
     */
    public ObsPUTEventHandler(final CDMINotificationQueue q) {
        super(q, "PUT");
    }
}
