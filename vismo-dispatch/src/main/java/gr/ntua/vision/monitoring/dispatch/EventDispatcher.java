package gr.ntua.vision.monitoring.dispatch;

/**
 * This is used to abstract away the details of generating/sending an event to the locally running vismo instance.
 */
public interface EventDispatcher {
    /**
     * Prepare to send an event. The {@link EventBuilder} object is used to keep track of the event fields.
     * 
     * @return an {@link EventBuilder} object.
     */
    EventBuilder newEvent();


    /**
     * Send the event.
     */
    void send();
}
