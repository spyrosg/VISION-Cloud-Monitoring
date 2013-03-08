package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class for aggregating over object service events.
 */
abstract class AggregationRule extends PeriodicRule {
    /***/
    protected static final String OBS_FIELD        = "transaction-duration";
    /***/
    private static final String   DELETE_OPERATION = "DELETE";
    /***/
    private static final String   GET_OPERATION    = "GET";
    /***/
    private static final Logger   log              = LoggerFactory.getLogger(AggregationRule.class);
    /***/
    private static final String   OPERATION_FIELD  = "operation";
    /***/
    private static final String   PUT_OPERATION    = "PUT";
    /***/
    private static final String   SRE_SERVICE      = "SRE";
    /***/
    private static final String   STORLET_KEY      = "storletType";
    /***/
    protected final String        topic;


    /**
     * Constructor.
     * 
     * @param engine
     * @param period
     * @param topic
     */
    public AggregationRule(final VismoRulesEngine engine, final long period, final String topic) {
        super(engine, period);
        this.topic = topic;
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#id()
     */
    @Override
    public String id() {
        return toString();
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<" + getClass().getSimpleName() + ", topic=" + topic + ", period=" + (period() / 1000) + "s>";
    }


    /**
     * @param dict
     * @param e
     */
    protected static void addRequiredFields(final HashMap<String, Object> dict, final MonitoringEvent e) {
        dict.put("timestamp", System.currentTimeMillis());
        dict.put("originating-service", e.originatingService());

        try {
            dict.put("originating-machine", e.originatingIP().getHostAddress());
        } catch (final UnknownHostException e1) {
            log.error("cannot get originating machine ip", e);
        }
    }


    /**
     * @param e
     * @param field
     * @return the value of the given field as a double number.
     */
    protected static Double getFieldValueAsDouble(final MonitoringEvent e, final String field) {
        final Object val = e.get(field);

        if (val == null) {
            log.warn("missing required field '{}' or is null; returning 0", field);

            return 0d;
        }

        if (val instanceof String) {
            log.warn("required field '{}' should be {}; try to parse it", field, Long.class);

            return Double.valueOf((String) val);
        }

        try {
            return (Double) val;
        } catch (final ClassCastException x) {
            log.trace("expecting field '{}' of type {} ...", field, Long.class);
            log.trace("but got value {} of type {}", val, val.getClass());
            log.trace("exception: ", x);

            return 0d;
        }
    }


    /**
     * @param e
     * @param field
     * @return the value of the given field as a long number.
     */
    protected static Long getFieldValueAsLong(final MonitoringEvent e, final String field) {
        final Object val = e.get(field);

        if (val == null) {
            log.warn("missing required field '{}' or is null; returning 0", field);

            return 0l;
        }

        if (val instanceof String) {
            log.warn("required field '{}' should be {}; try to parse it", field, Long.class);

            return Long.valueOf((String) val);
        }

        try {
            return (Long) val;
        } catch (final ClassCastException x) {
            log.trace("expecting field '{}' of type {} ...", field, Long.class);
            log.trace("but got value {} of type {}", val, val.getClass());
            log.trace("exception: ", x);

            return 0l;
        }
    }


    /**
     * Is this a complete object service event? Since we receive all events from object service, some of them are incomplete, in
     * the sense that contain parts of the request/response cycle.
     * 
     * @param e
     *            the event.
     * @return <code>true</code> iff the
     */
    protected static boolean isCompleteObsEvent(final MonitoringEvent e) {
        return e.get(OBS_FIELD) != null;
    }


    /**
     * Is this an SRE event?
     * 
     * @param e
     *            the event.
     * @return <code>true</code> iff the
     */
    protected static boolean isStorletEngineEvent(final MonitoringEvent e) {
        return SRE_SERVICE.equals(e.originatingService()) && e.get(STORLET_KEY) != null;
    }


    /**
     * @param eventList
     * @return the list of delete events.
     */
    protected static ArrayList<MonitoringEvent> selectDeleteEvents(final List< ? extends MonitoringEvent> eventList) {
        return selectEventsByOperation(eventList, DELETE_OPERATION);
    }


    /**
     * @param eventList
     * @param field
     * @return the list of events that contain the given field.
     */
    protected static ArrayList<MonitoringEvent> selectEventsByField(final List< ? extends MonitoringEvent> eventList,
            final String field) {
        final ArrayList<MonitoringEvent> list = new ArrayList<MonitoringEvent>();

        for (final MonitoringEvent e : eventList)
            if (e.get(field) != null)
                list.add(e);

        return list;
    }


    /**
     * @param eventList
     * @return the list of read events.
     */
    protected static ArrayList<MonitoringEvent> selectReadEvents(final List< ? extends MonitoringEvent> eventList) {
        return selectEventsByOperation(eventList, GET_OPERATION);
    }


    /**
     * @param eventList
     * @return the list of events that match only the given operation.
     */
    protected static ArrayList<MonitoringEvent> selectStorletEngineEvents(final List< ? extends MonitoringEvent> eventList) {
        final ArrayList<MonitoringEvent> newList = new ArrayList<MonitoringEvent>(eventList.size());

        for (final MonitoringEvent e : eventList)
            if (isStorletEngineEvent(e))
                newList.add(e);

        log.trace("have {} events for '{}'", newList.size(), SRE_SERVICE);

        return newList;
    }


    /**
     * @param eventList
     * @return the list of write events.
     */
    protected static ArrayList<MonitoringEvent> selectWriteEvents(final List< ? extends MonitoringEvent> eventList) {
        return selectEventsByOperation(eventList, PUT_OPERATION);
    }


    /**
     * @param eventList
     * @param operation
     * @return the list of events that match only the given operation.
     */
    private static ArrayList<MonitoringEvent> selectEventsByOperation(final List< ? extends MonitoringEvent> eventList,
            final String operation) {
        final ArrayList<MonitoringEvent> newList = new ArrayList<MonitoringEvent>(eventList.size());

        for (final MonitoringEvent e : eventList) {
            final String val = (String) e.get(OPERATION_FIELD);

            log.trace("event op={}", val);

            if (operation.equalsIgnoreCase(val))
                newList.add(e);
        }

        log.trace("have {} events for '{}'", newList.size(), operation);

        return newList;
    }
}
