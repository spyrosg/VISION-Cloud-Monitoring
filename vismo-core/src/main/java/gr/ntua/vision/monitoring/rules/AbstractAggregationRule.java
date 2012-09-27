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
    protected static final String OPERATION_FIELD  = "operation";
    /***/
    protected static final String SPECIAL_FIELD    = "transaction-duration";
    /***/
    private static final String   DELETE_OPERATION = "DELETE";
    /***/
    private static final String   GET_OPERATION    = "GET";
    /***/
    private static final Logger   log              = LoggerFactory.getLogger(AbstractAggregationRule.class);
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
     * @return
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

            return null;
        }
    }


    /**
     * @param e
     * @param field
     * @return
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

            return null;
        }
    }


    /**
     * @param eventList
     * @return
     */
    protected static ArrayList<Event> selectDeleteEvents(final List< ? extends Event> eventList) {
        return selectEventsByOperation(eventList, DELETE_OPERATION);
    }


    /**
     * @param eventList
     * @return
     */
    protected static ArrayList<Event> selectReadEvents(final List< ? extends Event> eventList) {
        return selectEventsByOperation(eventList, GET_OPERATION);
    }


    /**
     * @param eventList
     * @return
     */
    protected static ArrayList<Event> selectWriteEvents(final List< ? extends Event> eventList) {
        return selectEventsByOperation(eventList, PUT_OPERATION);
    }


    /**
     * @param eventList
     * @param operation
     * @return
     */
    private static ArrayList<Event> selectEventsByOperation(final List< ? extends Event> eventList, final String operation) {
        final ArrayList<Event> newList = new ArrayList<Event>(eventList.size());

        for (final Event e : eventList)
            if (operation.equalsIgnoreCase((String) e.get(OPERATION_FIELD)))
                newList.add(e);

        return newList;
    }
}
