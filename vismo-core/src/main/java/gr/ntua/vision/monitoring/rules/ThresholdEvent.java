package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * 
 */
class ThresholdEvent implements MonitoringEvent {
    /***/
    private static final VismoVMInfo vminfo = new VismoVMInfo();
    /***/
    private final String             id;
    /***/
    private final String             originatingService;
    /** this is the id of the rule triggered. */
    private final String             ruleId;
    /***/
    private final String             topic;
    /***/
    private final long               ts;
    /***/
    private final ViolationsList     violations;


    /**
     * Constructor.
     * 
     * @param ruleId
     * @param originatingService
     * @param topic
     * @param violations
     */
    public ThresholdEvent(final String ruleId, final String originatingService, final String topic,
            final ViolationsList violations) {
        this.ruleId = ruleId;
        this.originatingService = originatingService;
        this.topic = topic;
        this.violations = violations;
        this.ts = System.currentTimeMillis();
        this.id = UUID.randomUUID().toString();
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#get(java.lang.String)
     */
    @Override
    public Object get(final String key) {
        if ("violations".equals(key))
            return violations;
        if ("rule-id".equals(key))
            return ruleId;
        if ("id".equals(key))
            return id;

        if ("topic".equals(key))
            return topic();
        if ("timestamp".equals(key))
            return timestamp();
        if ("originating-machine".equals(key))
            return vminfo.getAddress().getHostAddress();
        if ("originating-service".equals(key))
            return originatingService();

        return null;
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#originatingIP()
     */
    @Override
    public InetAddress originatingIP() throws UnknownHostException {
        return vminfo.getAddress();
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#originatingService()
     */
    @Override
    public String originatingService() {
        return originatingService;
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#serialize()
     */
    @SuppressWarnings("unchecked")
    @Override
    public String serialize() {
        final JSONObject o = new JSONObject();
        final JSONArray arr = new JSONArray();

        for (int i = 0; i < violations.size(); ++i) {
            final Violation v = violations.get(i);
            final JSONObject o1 = new JSONObject();

            o1.put("value", v.eventValue);
            o1.put("metric", v.metric);
            o1.put("threshold", v.threshold);
            arr.add(o1);
        }

        o.put("id", id);
        o.put("rule-id", ruleId);
        o.put("violations", arr);
        o.put("timestamp", ts);
        o.put("topic", topic);
        o.put("originating-service", originatingService);
        o.put("originating-machine", vminfo.getAddress().getHostAddress());

        return o.toJSONString();
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#timestamp()
     */
    @Override
    public long timestamp() {
        return ts;
    }


    /**
     * @see gr.ntua.vision.monitoring.events.MonitoringEvent#topic()
     */
    @Override
    public String topic() {
        return topic;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<ThresholdEvent: " + ruleId + " of topic: " + topic + " at timestamp: " + ts + ", violations: " + violations
                + ">";
    }
}
