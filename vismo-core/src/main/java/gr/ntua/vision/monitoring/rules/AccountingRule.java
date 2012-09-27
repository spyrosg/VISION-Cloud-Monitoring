package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class AccountingRule extends AbstractAggregationRule {
    /***/
    private static final Logger log    = LoggerFactory.getLogger(AccountingRule.class);
    /** one second in milliseconds */
    private static long         MILLIS = 1000;
    /***/
    private static final String TOPIC  = "Accounting";


    /**
     * Constructor.
     * 
     * @param period
     */
    public AccountingRule(final long period) {
        super(TOPIC, period);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#aggregate(long, java.util.List)
     */
    @SuppressWarnings("unchecked")
    @Override
    public AggregationResultEvent aggregate(final long aggregationStartTime, final List< ? extends Event> eventList) {
        @SuppressWarnings("rawtypes")
        final HashMap dict = getAccountingEventObject(eventList, aggregationStartTime);

        return new VismoAggregationResultEvent(dict);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#matches(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public boolean matches(final Event e) {
        // FIXME: add a field for events coming from vismo_dispatch
        return e.get(SPECIAL_FIELD) != null;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<" + this.getClass().getSimpleName() + ", running every " + (period / 1000) + " second(s)>";
    }


    /**
     * @param eventList
     * @param aggregationStartTime
     * @return
     */
    private static HashMap<String, Object> getAccountingEventObject(final List< ? extends Event> eventList,
            final long aggregationStartTime) {
        final HashMap<String, Object> dict = new HashMap<String, Object>();

        dict.put("reads", transformReadList(selectReadEvents(eventList)));
        dict.put("writes", transformWriteList(selectWriteEvents(eventList)));
        dict.put("deletes", transformDeleteList(selectDeleteEvents(eventList)));
        // TODO: dict.put("storlets", );
        dict.put("topic", TOPIC);
        dict.put("tStart", aggregationStartTime);
        dict.put("tEnd", System.currentTimeMillis());

        return dict;
    }


    /**
     * @param list
     * @param operation
     * @return
     */
    private static ArrayList<HashMap<String, Object>> transformByOperation(final ArrayList<Event> list, final String operation) {
        final ArrayList<HashMap<String, Object>> newList = new ArrayList<HashMap<String, Object>>(list.size());

        for (final Event e : list) {
            final HashMap<String, Object> o = transformEvent(e);

            o.put("eventType", operation);
            newList.add(o);
        }

        return newList;
    }


    /**
     * @param eventList
     * @return the list of delete events as prescribed by accounting.
     */
    private static ArrayList<HashMap<String, Object>> transformDeleteList(final ArrayList<Event> eventList) {
        log.trace("got {} delete events", eventList.size());
        return transformByOperation(eventList, "delete");
    }


    /**
     * @param e
     * @return the event object as prescribed by accounting.
     */
    private static HashMap<String, Object> transformEvent(final Event e) {
        final HashMap<String, Object> o = new HashMap<String, Object>();
        final long ts = e.timestamp();
        final double duration = MILLIS * (Double) e.get("transaction-duration");

        o.put("tStart", (long) (ts - duration));
        o.put("tEnd", ts);

        o.put("tenantID", e.get("tenant"));
        o.put("userID", e.get("user"));
        o.put("containerID", e.get("container"));
        o.put("objectID", e.get("object"));
        o.put("service", e.originatingService());

        o.put("count", 1);
        // NOTE: N/A o.put("value",);
        o.put("bandwidth", e.get("transaction-throughput"));
        o.put("replicas", 1);

        o.put("size", e.get("content-size"));

        return o;
    }


    /**
     * @param eventList
     * @return the list of read events as prescribed by accounting.
     */
    private static ArrayList<HashMap<String, Object>> transformReadList(final ArrayList<Event> eventList) {
        log.trace("got {} read events", eventList.size());
        return transformByOperation(eventList, "read");
    }


    /**
     * @param eventList
     * @return the list of write events as prescribed by accounting.
     */
    private static ArrayList<HashMap<String, Object>> transformWriteList(final ArrayList<Event> eventList) {
        log.trace("got {} write events", eventList.size());
        return transformByOperation(eventList, "write");
    }
}
