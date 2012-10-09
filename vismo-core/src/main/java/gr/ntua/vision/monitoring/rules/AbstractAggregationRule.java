package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
abstract class AbstractAggregationRule implements AggregationRule {
    /***/
    protected static final String OBS_FIELD        = "transaction-duration";
    /***/
    private static final String   DELETE_OPERATION = "DELETE";
    /***/
    private static final String   GET_OPERATION    = "GET";
    /***/
    private static final Logger   log              = LoggerFactory.getLogger(AbstractAggregationRule.class);
    /***/
    private static final String   OPERATION_FIELD  = "operation";
    /***/
    private static final String   PUT_OPERATION    = "PUT";
    /***/
    protected final long          period;
    /***/
    protected final String        topic;


    /**
     * Constructor.
     * 
     * @param topic
     * @param period
     */
    public AbstractAggregationRule(final String topic, final long period) {
        this.topic = topic;
        this.period = period;
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#aggregationPeriod()
     */
    @Override
    public long aggregationPeriod() {
        return period;
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AbstractAggregationRule other = (AbstractAggregationRule) obj;
        if (period != other.period)
            return false;
        if (topic == null) {
            if (other.topic != null)
                return false;
        } else if (!topic.equals(other.topic))
            return false;
        return true;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (period ^ (period >>> 32));
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        return result;
    }


    /**
     * @param e
     * @param field
     * @return the value of the given field as a double number.
     */
    protected static Double getFieldValueAsDouble(final Event e, final String field) {
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
    protected static Long getFieldValueAsLong(final Event e, final String field) {
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
    protected static boolean isCompleteObsEvent(final Event e) {
        return e.get(OBS_FIELD) != null;
    }


    /**
     * @param eventList
     * @return the list of delete events.
     */
    protected static ArrayList<Event> selectDeleteEvents(final List< ? extends Event> eventList) {
        return selectEventsByOperation(eventList, DELETE_OPERATION);
    }


    /**
     * @param eventList
     * @param field
     * @return the list of events that contain the given field.
     */
    protected static ArrayList<Event> selectEventsByField(final List< ? extends Event> eventList, final String field) {
        final ArrayList<Event> list = new ArrayList<Event>();

        for (final Event e : eventList)
            if (e.get(field) != null)
                list.add(e);

        return list;
    }


    /**
     * @param eventList
     * @return the list of read events.
     */
    protected static ArrayList<Event> selectReadEvents(final List< ? extends Event> eventList) {
        return selectEventsByOperation(eventList, GET_OPERATION);
    }


    /**
     * @param eventList
     * @return the list of write events.
     */
    protected static ArrayList<Event> selectWriteEvents(final List< ? extends Event> eventList) {
        return selectEventsByOperation(eventList, PUT_OPERATION);
    }


    /**
     * @param eventList
     * @param operation
     * @return the list of events that match only the given operation.
     */
    private static ArrayList<Event> selectEventsByOperation(final List< ? extends Event> eventList, final String operation) {
        final ArrayList<Event> newList = new ArrayList<Event>(eventList.size());

        for (final Event e : eventList) {
        	final String val = (String) e.get(OPERATION_FIELD);
        	
        	log.trace("event op={}", val);
        	
            if (operation.equalsIgnoreCase(val))
                newList.add(e);
        }

        log.trace("have {} events for '{}'", newList.size(), operation);
        
        return newList;
    }
}
