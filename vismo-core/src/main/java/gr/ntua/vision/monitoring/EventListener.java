package gr.ntua.vision.monitoring;

/**
 * An event listener is used to pass around events to interested parties.
 */
public interface EventListener {
    /**
     * Notify the listener of the event.
     * 
     * @param message
     *            the message received.
     */
    void notify(String message);
}
