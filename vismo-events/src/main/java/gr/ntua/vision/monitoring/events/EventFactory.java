package gr.ntua.vision.monitoring.events;

import java.util.Map;


/**
 * This is used to construct {@link MonitoringEvent}s. Clients can either provide a JSON string representation of an event, or a
 * map of key value pairs.
 */
public interface EventFactory {
    /**
     * Construct an event out of the given map.
     * 
     * @param map
     *            the map.
     * @return the constructed {@link MonitoringEvent}.
     */
    MonitoringEvent createEvent(final Map<String, Object> map);


    /**
     * Deserialize and construct an event.
     * 
     * @param str
     *            the string that contains a serialized event.
     * @return on success, a new {@link MonitoringEvent}, <code>null</code> otherwise.
     */
    MonitoringEvent createEvent(final String str);
}
