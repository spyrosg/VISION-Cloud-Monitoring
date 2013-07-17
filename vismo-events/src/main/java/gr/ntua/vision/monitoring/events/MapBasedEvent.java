package gr.ntua.vision.monitoring.events;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;


/**
 * The default implementation of {@link MonitoringEvent}'s, based on {@link Map}s.
 */
class MapBasedEvent implements MonitoringEvent {
    /***/
    private static String[]           REQUIRED_FIELDS = { "timestamp", "originating-service", "originating-machine" };
    /** the map. */
    private final Map<String, Object> map;


    /**
     * Constructor.
     * 
     * @param map
     *            a map of key/values.
     */
    MapBasedEvent(final Map<String, Object> map) {
        assertHaveFields(map, REQUIRED_FIELDS);
        this.map = new HashMap<String, Object>(map);
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#get(java.lang.String)
     */
    @Override
    public Object get(final String key) {
        return map.get(key);
    }


    /**
     * @throws UnknownHostException
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#originatingIP()
     */
    @Override
    public InetAddress originatingIP() throws UnknownHostException {
        return InetAddress.getByName((String) map.get("originating-machine"));
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#originatingService()
     */
    @Override
    public String originatingService() {
        return (String) map.get("originating-service");
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#serialize()
     */
    @Override
    public String serialize() {
        return JSONObject.toJSONString(map);
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#timestamp()
     */
    @Override
    public long timestamp() {
        return (Long) map.get("timestamp");
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#topic()
     */
    @Override
    public String topic() {
        return (String) map.get("topic");
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "vismo-event";
    }


    /**
     * Check that the map contains event required fields.
     * 
     * @param map
     *            the map to check.
     * @param requiredFields
     */
    protected static void assertHaveFields(final Map<String, Object> map, final String[] requiredFields) {
        for (final String field : requiredFields)
            requireField(map, field);
    }


    /**
     * Check that given key is member of the map; if not, raise an {@link Error}.
     * 
     * @param map
     *            the map to check.
     * @param key
     *            the key to check.
     * @throws Error
     *             when the <code>key</code> is missing.
     */
    protected static void requireField(final Map<String, Object> map, final String key) {
        if (!map.containsKey(key))
            throw new Error("event missing required field: " + key);
    }
}
