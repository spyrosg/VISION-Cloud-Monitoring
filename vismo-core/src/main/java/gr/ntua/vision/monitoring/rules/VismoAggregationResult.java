package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.VismoVMInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


/**
 * 
 */
public class VismoAggregationResult implements AggregationResult {
    /** this host's ip address. */
    private static final String       ip = new VismoVMInfo().getAddress().getHostAddress();
    /***/
    private final Map<String, Object> dict;


    /**
     * Constructor.
     * 
     * @param dict
     *            a dictionary of key/values.
     */
    public VismoAggregationResult(final Map<String, Object> dict) {
        this.dict = dict;
        appendDefaultFields();
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#get(java.lang.String)
     */
    @Override
    public Object get(final String key) {
        return dict.get(key);
    }


    /**
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
     * @see gr.ntua.vision.monitoring.rules.AggregationResult#settEnd(long)
     */
    @Override
    public void settEnd(final long ts) {
        put("tEnd", ts);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationResult#settStart(long)
     */
    @Override
    public void settStart(final long ts) {
        put("tStart", ts);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationResult#tEnd()
     */
    @Override
    public long tEnd() {
        return (Long) get("tEnd");
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
        return "#<VismoAggregationResultEvent: for period[" + toDate(tStart()) + ", " + toDate(tEnd()) + "]>";
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationResult#tStart()
     */
    @Override
    public long tStart() {
        return (Long) get("tStart");
    }


    /**
     * Add required fields to the event.
     */
    private void appendDefaultFields() {
        dict.put("timestamp", System.currentTimeMillis());
        dict.put("id", UUID.randomUUID().toString());
        dict.put("originating-machine", ip);
    }


    /**
     * @param key
     * @param val
     */
    private void put(final String key, final Object val) {
        dict.put(key, val);
    }


    /**
     * @param t
     * @return a human readable representation of the given date.
     */
    private static String toDate(final long t) {
        return new Date(t).toString();
    }
}
