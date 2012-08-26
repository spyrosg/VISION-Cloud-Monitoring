package gr.ntua.vision.monitoring.events;

/**
 *
 */
public interface EventFactory {
    /**
     * Deserialize an event from the given string.
     * 
     * @param str
     *            a string that contains a serialized event.
     * @return on success, a new {@link Event}, <code>null</code> otherwise.
     */
    Event createEvent(final String str);
}
