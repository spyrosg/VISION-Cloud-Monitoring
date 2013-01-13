package gr.ntua.vision.monitoring.events;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;


/**
 * An implementation of vismo events based on {@link Map}s.
 */
public class VismoEvent implements MonitoringEvent {
    /** the dictionary of key/values. */
    @SuppressWarnings("rawtypes")
    private final Map dict;


    /**
     * Constructor.
     * 
     * @param dict
     *            a dictionary of key/values.
     */
    protected VismoEvent(@SuppressWarnings("rawtypes") final Map dict) {
        this.dict = dict;
    }


    /**
     * @return the underlying dictionary.
     */
    @SuppressWarnings("rawtypes")
    public Map dict() {
        return dict;
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
     * @param key
     * @param value
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public void put(final Object key, final Object value) {
        dict.put(key, value);
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
}
