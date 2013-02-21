package performance;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;

/**
 * @author tmessini
 *
 * simple consumer handle
 */
class SimpleConsumerHandler implements EventHandler {

    /***/
    private int    noReceivedEvents;
    /***/
    private double maxLatency;


    /**
     * 
     */
    public SimpleConsumerHandler() {
        noReceivedEvents = 0;
        maxLatency = 0;
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
    public double getMaxLatencyInSecs() {
        return (maxLatency / 1000.0);
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void handle(final MonitoringEvent e) {
        if (e != null) {
            ++noReceivedEvents;
            if (e.timestamp() > maxLatency)
                maxLatency = (System.currentTimeMillis() - e.timestamp());
        }

    }
}