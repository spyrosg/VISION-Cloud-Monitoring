package gr.ntua.vision.monitoring.queues;

/**
 * Exception thrown when the user cannot register a queue.
 */
@SuppressWarnings("serial")
public class QueuesRegistrationException extends RuntimeException {
    /**
     * Constructor.
     * 
     * @param message
     */
    public QueuesRegistrationException(final String message) {
        super(message);
    }
}
