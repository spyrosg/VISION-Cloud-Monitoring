package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class AggregationOnNumberOfRequests implements AggregationRule {
    /***/
    private static final String DICT          = "!dict";
    /***/
    private static final Logger log           = LoggerFactory.getLogger(AggregationOnNumberOfRequests.class);
    /***/
    private static final String SPECIAL_FIELD = "transaction-duration";
    /***/
    private final String        newField;
    /***/
    private final String        operation;


    /**
     * @param operation
     * @param resultField
     */
    public AggregationOnNumberOfRequests(final String operation, final String resultField) {
        this.operation = operation;
        this.newField = resultField;
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#aggregate(long, java.util.List)
     */
    @SuppressWarnings("unchecked")
    @Override
    public AggregationResultEvent aggregate(final long aggregationStartTime, final List< ? extends Event> eventList) {
        return new VismoAggregationResultEvent(appendNewField(eventList, eventList.size()));
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#matches(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public boolean matches(final Event e) {
        final String op = (String) e.get("operation");

        // FIXME: add a field for events coming from vismo_dispatch
        return e.get(SPECIAL_FIELD) != null && op.equals(operation);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<" + this.getClass().getSimpleName() + "[" + operation + "] with new field '" + newField + "'>";
    }


    /**
     * @param eventList
     * @param count
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map appendNewField(final List< ? extends Event> eventList, final int count) {
        final Event firstEvent = eventList.get(0);
        final Event lastEvent = eventList.get(eventList.size() - 1);
        final Map dict = (Map) lastEvent.get(DICT);

        // FIXME: these should be gotten off the timer
        dict.put("tStart", firstEvent.timestamp());
        dict.put("tEnd", lastEvent.timestamp());
        dict.put(newField, count);

        return dict;
    }
}
