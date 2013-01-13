package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.events.VismoEvent;

import java.util.Date;
import java.util.Map;
import java.util.UUID;


/**
 * 
 */
public class VismoAggregationResult extends VismoEvent implements AggregationResult {
    /** this host's ip address. */
    private static final String ip = new VismoVMInfo().getAddress().getHostAddress();


    /**
     * Constructor.
     * 
     * @param dict
     *            a dictionary of key/values.
     */
    public VismoAggregationResult(final Map<String, Object> dict) {
        super(dict);
        appendDefaultFields();
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationResult#tEnd()
     */
    @Override
    public long tEnd() {
        return (Long) get("tEnd");
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
        put("timestamp", System.currentTimeMillis());
        put("id", UUID.randomUUID().toString());
        put("originating-machine", ip);
    }


    /**
     * @param t
     * @return a human readable representation of the given date.
     */
    private static String toDate(final long t) {
        return new Date(t).toString();
    }
}
