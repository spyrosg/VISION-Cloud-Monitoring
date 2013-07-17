package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.events.VismoEventFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


/**
 *
 */
class ThresholdEventFactory {
    /***/
    private static final VismoEventFactory factory = new VismoEventFactory();


    /**
     * Constructor.
     */
    private ThresholdEventFactory() {
    }


    /**
     * @param ruleId
     * @param topic
     * @param e
     * @param violations
     * @return the event that describes the violations, as a {@link MonitoringEvent}.
     */
    public static MonitoringEvent newEvent(final String ruleId, final String topic, final MonitoringEvent e,
            final ViolationsList violations) {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        final ArrayList<Object> arr = new ArrayList<Object>();

        for (int i = 0; i < violations.size(); ++i) {
            final Violation v = violations.get(i);
            final HashMap<String, Object> o1 = new HashMap<String, Object>();

            o1.put("value", v.eventValue);
            o1.put("metric", v.metric);
            o1.put("threshold", v.threshold);
            arr.add(o1);
        }

        map.put("id", UUID.randomUUID().toString());
        map.put("rule-id", ruleId);
        map.put("violations", arr);
        map.put("timestamp", System.currentTimeMillis());
        map.put("topic", topic);
        map.put("originating-service", e.originatingService());
        map.put("originating-machine", e.get("originating-machine"));

        return factory.createEvent(map);
    }
}
