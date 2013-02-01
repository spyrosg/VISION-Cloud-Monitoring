package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 * An {@link AggregationResult} is an event that corresponds to a period of time, the <em>aggregation</em> period.
 */
public interface AggregationResult extends MonitoringEvent {
    /**
     * This marks the end of the aggregation period.
     * 
     * @param ts
     *            the timestamp.
     */
    void settEnd(long ts);


    /**
     * This marks the start of the aggregation period.
     * 
     * @param ts
     *            the timestamp.
     */
    void settStart(long ts);


    /**
     * @return the time end period of the aggregation.
     */
    long tEnd();


    /**
     * @return the start time period of the aggregation.
     */
    long tStart();
}
