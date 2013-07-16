package gr.ntua.vision.monitoring.queues;

/**
 * Exception thrown when a user asks for a non existent queue.
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
