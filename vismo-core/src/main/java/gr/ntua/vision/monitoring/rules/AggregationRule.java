package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.List;


/**
 * This is used to represent an operation on a sequence of events, over a period of time.
 */
public interface AggregationRule {
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
     * @return in milliseconds the period of the aggregation.
     */
    long aggregationPeriod();


    /**
     * This is the <strong>when</strong> part of the rule. If this is <code>true</code> the rule is activated, i.e, will be
     * consequently run.
     * 
     * @param e
     * @return <code>true</code> if the rule applies to the event, <code>false</code> otherwise.
     */
    boolean matches(Event e);
}
