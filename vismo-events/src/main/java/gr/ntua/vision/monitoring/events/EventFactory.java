package gr.ntua.vision.monitoring.events;

/**
 * This is used to deserialize events.
 */
public interface EventFactory {
    /**
     * Deserialize an event from the given string.
     * 
     * @param str
     *            a string that contains a serialized event.
     * @return on success, a new {@link MonitoringEvent}, <code>null</code> otherwise.
     */
    MonitoringEvent createEvent(final String str);
}
