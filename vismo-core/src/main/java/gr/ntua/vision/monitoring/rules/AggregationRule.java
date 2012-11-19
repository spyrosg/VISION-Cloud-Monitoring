package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.List;


/**
 * This is used to represent an operation on a sequence of events.
 */
public interface AggregationRule extends Rule {
    /**
     * This is the <strong>then</strong> part of the rule. Perform the aggregation on a number of events that have previously
     * matched the rule.
     * 
     * @param eventList
     *            the list of events to aggregate.
     * @return the result.
     */
    AggregationResult aggregate(List< ? extends Event> eventList);


    /**
     * @param e
     */
    void collect(Event e);
}
