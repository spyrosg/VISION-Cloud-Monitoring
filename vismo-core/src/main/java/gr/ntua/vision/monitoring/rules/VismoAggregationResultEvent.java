package gr.ntua.vision.monitoring.rules;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


/**
 * TODO: somehow link with {@link VismoEvent}.
 */
public class VismoAggregationResultEvent implements AggregationResultEvent {
    /** this is used to get a hold of the whole dict, for serialization reasons. */
    private static final String       DICT_KEY = "!dict";
    /** the dictionary of key/values. */
    private final Map<String, Object> dict;


    /**
     * Constructor.
     * 
     * @param dict
     *            a dictionary of key/values.
     */
    public VismoAggregationResultEvent(final Map<String, Object> dict) {
        this.dict = dict;
        removeUnessecaryFields(dict);
        addBasicFields(dict);
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
        return InetAddress.getByName((String) get1("originating-ip"));
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#originatingService()
     */
    @Override
    public String originatingService() {
        return (String) get1("originating-service");
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationResultEvent#tEnd()
     */
    @Override
    public long tEnd() {
        return (Long) get1("tEnd");
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#timestamp()
     */
    @Override
    public long timestamp() {
        return (Long) get1("timestamp");
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#topic()
     */
    @Override
    public String topic() {
        return (String) get1("topic");
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<VismoAggregationResultEvent: for period[" + toDate(tStart()) + ", " + toDate(tEnd()) + "]>";
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationResultEvent#tStart()
     */
    @Override
    public long tStart() {
        return (Long) get1("tStart");
    }


    /**
     * @param dict
     */
    private static void addBasicFields(final Map<String, Object> dict) {
        dict.put("timestamp", dict.get("tStart"));
        dict.put("id", UUID.randomUUID().toString());
    }


    /**
     * @param key
     * @return
     */
    private Object get1(final String key) {
        return dict.get(key);
    }


    /**
     * @param dict
     */
    private static void removeUnessecaryFields(@SuppressWarnings("rawtypes") final Map dict) {
        dict.remove("transaction-throughput");
        dict.remove("content-size");
        dict.remove("object");
        dict.remove("operation");
        dict.remove("tenant");
        dict.remove("container");
        dict.remove("transaction-latency");
        dict.remove("transaction-duration");
    }


    /**
     * @param t
     * @return
     */
    private static String toDate(final long t) {
        return new Date(t).toString();
    }
}
