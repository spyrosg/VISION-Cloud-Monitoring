package gr.ntua.vision.monitoring.queues;

/**
 *
 */
public class ObsGETEventHandler extends ObsEventHandler {
    /**
     * Constructor.
     * 
     * @param q
     */
    public ObsGETEventHandler(final CDMINotificationQueue q) {
        super(q, "GET");
    }
}
