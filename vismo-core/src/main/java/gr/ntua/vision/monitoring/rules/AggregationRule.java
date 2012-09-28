package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.List;


/**
 * This is used to represent an operation on a sequence of events, over a period of time.
 */
public interface AggregationRule {
    /**
     * @param eventList
     *            the list of events to aggregate.
     * @return the result.
     */
    AggregationResult aggregate(List< ? extends Event> eventList);


    /**
     * @return in milliseconds the period of the aggregation.
     */
    long aggregationPeriod();


    /**
     * @param e
     * @return
     */
    boolean matches(Event e);
}
