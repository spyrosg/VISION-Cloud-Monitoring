package gr.ntua.vision.monitoring.dispatch;

/**
 * This is used to abstract away the details of generating/sending an event to the locally running vismo instance.
 */
public interface EventDispatcher {
    /**
     * Prepare to send an event.
     * 
     * @return a helper object that can produce events.
     */
    EventBuilder newEvent();


    /**
     * Send the event.
     */
    void send();
}
