package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;


/**
 * 
 */
public interface AggregationResultEvent extends Event {
    /**
     * @return
     */
    long tEnd();


    /**
     * @return
     */
    long tStart();
}
