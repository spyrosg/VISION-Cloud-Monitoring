package gr.ntua.vision.monitoring.queues;

/**
 * Class of exceptions thrown when the user has performed an invalid operation.
 */
@SuppressWarnings("serial")
public class CDMIQueueException extends RuntimeException {
    /**
     * Constructor.
     * 
     * @param message
     */
    public CDMIQueueException(final String message) {
        super(message);
    }
}
