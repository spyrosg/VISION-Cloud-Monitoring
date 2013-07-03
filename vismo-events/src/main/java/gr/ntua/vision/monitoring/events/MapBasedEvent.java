package gr.ntua.vision.monitoring.events;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;


/**
 * An implementation of vismo events based on {@link Map}s.
 */
class MapBasedEvent implements MonitoringEvent {
    /***/
    private static final List<String> REQUIRED_FIELDS = Arrays.asList("timestamp", "originating-service", "originating-machine");
    /** the dictionary of key/values. */
    private final Map<String, Object> dict;


    /**
     * Constructor.
     * 
     * @param dict
     *            a dictionary of key/values.
     */
    MapBasedEvent(final Map<String, Object> dict) {
        assertHaveFields(dict, REQUIRED_FIELDS);
        this.dict = new HashMap<String, Object>(dict);
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#get(java.lang.String)
     */
    @Override
    public Object get(final String key) {
        return dict.get(key);
    }


    /**
     * @throws UnknownHostException
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#originatingIP()
     */
    @Override
    public InetAddress originatingIP() throws UnknownHostException {
        return InetAddress.getByName((String) dict.get("originating-machine"));
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#originatingService()
     */
    @Override
    public String originatingService() {
        return (String) dict.get("originating-service");
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#serialize()
     */
    @Override
    public String serialize() {
        return JSONObject.toJSONString(dict);
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#timestamp()
     */
    @Override
    public long timestamp() {
        return (Long) dict.get("timestamp");
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#topic()
     */
    @Override
    public String topic() {
        return (String) dict.get("topic");
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
    protected static void assertHaveFields(final Map<String, Object> map, final List<String> requiredFields) {
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
