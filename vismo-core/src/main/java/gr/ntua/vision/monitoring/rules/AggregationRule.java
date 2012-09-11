package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.List;


/**
 * This is used to represent an operation on a sequence of events, over a period of time.
 */
public interface AggregationRule {
    /**
     * @param aggregationPeriodTimestamp
     *            this is the time instance the aggregation commenced.
     * @param eventList
     *            the list of events to aggregate.
     * @return the result.
     */
    AggregationResultEvent aggregate(final long aggregationPeriodTimestamp, List< ? extends Event> eventList);


    /**
     * @param e
     * @return
     */
    boolean matches(Event e);
}
