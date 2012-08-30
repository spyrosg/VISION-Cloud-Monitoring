package gr.ntua.vision.monitoring;

/**
 * This is used to abstract away the details of generating/sending an event to the locally running vismo instance.
 */
public interface EventDispatcher {
    /**
     * Send the event.
     */
    void send();
}
