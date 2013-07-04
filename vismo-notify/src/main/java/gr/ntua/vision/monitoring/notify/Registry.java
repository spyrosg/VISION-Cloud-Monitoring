package gr.ntua.vision.monitoring.notify;

/**
 *
 */
public interface Registry {
    /**
     * Register the handler to receive events from all topics.
     * 
     * @param handler
     *            the handler.
     * @return the {@link EventHandlerTask} for the given handler.
     */
    EventHandlerTask registerToAll(EventHandler handler);


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
}
