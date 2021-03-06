package gr.ntua.vision.monitoring.notify;

/**
 *
 */
public interface Registry {
    /**
     * This should be called out the end of the application. Stop receiving any events; halt any running event handlers; don't
     * accept other registrations.
     */
    void halt();


    /**
     * Register the handler to receive events only of the given topic.
     * 
     * @param topic
     *            the event topic.
     * @param handler
     *            the handler.
     * @return the {@link EventHandlerTask} for the given handler.
     */
    EventHandlerTask register(String topic, EventHandler handler);


    /**
     * Register the handler to receive events from all topics.
     * 
     * @param handler
     *            the handler.
     * @return the {@link EventHandlerTask} for the given handler.
     */
    EventHandlerTask registerToAll(EventHandler handler);


    /**
     * Unregister the handler. No new events will be received by the handler.
     * 
     * @param handler
     *            the handler.
     */
    void unregister(EventHandler handler);
}
