package gr.ntua.vision.monitoring.queues;

/**
 *
 */
class ObsPUTEventHandler extends ObsEventHandler {
    /**
     * Constructor.
     * 
     * @param q
     */
    public ObsPUTEventHandler(final CDMIQueue q) {
        super(q, "PUT");
    }
}
