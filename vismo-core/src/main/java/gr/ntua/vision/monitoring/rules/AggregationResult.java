package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;


/**
 * 
 */
public interface AggregationResult extends Event {
    /**
     * @param t
     */
    void puttEnd(final long t);


    /**
     * @param t
     */
    void puttStart(final long t);


    /**
     * @return the time end period of the aggregation.
     */
    long tEnd();


    /**
     * @return the start time period of the aggregation.
     */
    long tStart();
}
