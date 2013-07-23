package gr.ntua.vision.monitoring.queues;

/**
 *
 */
class ObsGETEventHandler extends ObsEventHandler {
    /**
     * Constructor.
     * 
     * @param q
     */
    public ObsGETEventHandler(final CDMIQueue q) {
        super(q, "GET");
    }
}
