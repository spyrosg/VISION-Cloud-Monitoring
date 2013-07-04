package gr.ntua.vision.monitoring.queues;

/**
 *
 */
@SuppressWarnings("serial")
public class QueuesRegistrationError extends RuntimeException {
    /**
     * Constructor.
     * 
     * @param message
     */
    public QueuesRegistrationError(final String message) {
        super(message);
    }
}
