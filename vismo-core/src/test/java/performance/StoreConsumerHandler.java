package performance;


import java.util.concurrent.ConcurrentHashMap;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;


/**
 * @author tmessini
 *
 * simple consumer handle
 */
class StoreConsumerHandler implements EventHandler {

    /***/
    private int    noReceivedEvents;
    /***/
    private String field;
    /***/
    private ConcurrentHashMap<Long, Long> fieldStore=new ConcurrentHashMap<Long, Long>();



    /**
     * stores in HashMap events fields for statistical
     * analysis.
     * 
     * @param field 
     * 
     */
    public StoreConsumerHandler(String field) {
        noReceivedEvents = 0;
        this.field = field;
    }


    /**
     * get the number of events in the consumer side.
     * 
     * @return the number of received events.
     */
    public int getNoReceivedEvents() {
        return noReceivedEvents;
    }


    /**
     * get the maximum latency in seconds for all the event
     * 
     * @return max latency
     */
    public ConcurrentHashMap<Long, Long> getEventsStore() {
        return fieldStore;
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void handle(final MonitoringEvent e) {
        if (e != null) {
            ++noReceivedEvents;            
            if(field.equals("latency"))
            {
                fieldStore.put((Long)e.get("latch-id"), System.currentTimeMillis()-(Long)e.get("timestamp"));
            }
        }
    }
}