package gr.ntua.vision.monitoring.queues;

/**
 *
 */
@SuppressWarnings("serial")
public class NoSuchQueueException extends RuntimeException {
    /**
     * Constructor.
     * 
     * @param message
     */
    public NoSuchQueueException(final String message) {
        super(message);
    }
}
