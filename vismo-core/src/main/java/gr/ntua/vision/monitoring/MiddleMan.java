package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AggregationResultEvent;

/**
 * 
 */
public class MiddleMan {
    private final VismoCloudElement element;
    private final AggregationWorker worker;


    /**
     * Constructor.
     * 
     * @param element
     * @param worker
     */
    public MiddleMan(VismoCloudElement element, AggregationWorker worker) {
        this.element = element;
        this.worker = worker;
    }
    
    /**
     * 
     */
    private void perform() {
        final Event e = receiveEventFromSource();
        
        worker.pass(e);
        aggregation.performInTimer();
        
        final AggregationResultEvent result;
        sendToSink(result);
    }
}
