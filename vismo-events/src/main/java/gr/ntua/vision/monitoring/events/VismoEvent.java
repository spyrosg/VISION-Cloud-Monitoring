package gr.ntua.vision.monitoring.events;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;


/**
 * An implementation of vismo events.
 */
class VismoEvent implements Event {
    /** this is used to get a hold of the whole dict, for serialization reasons. */
    private static final String DICT_KEY = "!dict";
    /** the dictionary of key/values. */
    @SuppressWarnings("rawtypes")
    private final Map           dict;


    /**
     * Constructor.
     * 
     * @param dict
     *            a dictionary of key/values.
     */
    VismoEvent(@SuppressWarnings("rawtypes") final Map dict) {
        this.dict = dict;
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#get(java.lang.String)
     */
    @Override
    public Object get(final String key) {
        if (key.equals(DICT_KEY))
            return dict;

        return dict.get(key);
    }


    /**
     * @throws UnknownHostException
     * @see gr.ntua.vision.monitoring.events.Event#originatingIP()
     */
    @Override
    public InetAddress originatingIP() throws UnknownHostException {
        return InetAddress.getByName((String) dict.get("originating-ip"));
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#originatingService()
     */
    @Override
    public String originatingService() {
        return (String) dict.get("originating-service");
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#timestamp()
     */
    @Override
    public long timestamp() {
        return (Long) dict.get("timestamp");
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#topic()
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
