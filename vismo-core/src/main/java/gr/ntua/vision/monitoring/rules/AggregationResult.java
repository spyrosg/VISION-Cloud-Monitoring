package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 * An {@link AggregationResult} is an event that corresponds to a period of time, the <em>aggregation</em> period.
 */
public interface AggregationResult extends MonitoringEvent {
    /**
     * @return the time end period of the aggregation.
     */
    long tEnd();


    /**
     * @return the start time period of the aggregation.
     */
    long tStart();
}
