package integration.tests;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;

import java.util.concurrent.CountDownLatch;


/**
 * This handler is used to release the latch when the expected number of events is received.
 */
class ConsumerHandler implements EventHandler {
    /***/
    private final CountDownLatch latch;
    /***/
    private final int            noExpectedEvents;
    /***/
    private int                  noReceivedEvents = 0;


    /**
     * Constructor.
     * 
     * @param latch
     * @param noExpectedEvents
     */
    public ConsumerHandler(final CountDownLatch latch, final int noExpectedEvents) {
        this.latch = latch;
        this.noExpectedEvents = noExpectedEvents;
    }


    /**
     * @return the number of received events.
     */
    public int getNoReceivedEvents() {
        return noReceivedEvents;
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void handle(final MonitoringEvent e) {
        if (e == null)
            return;

        ++noReceivedEvents;

        if (noExpectedEvents == noReceivedEvents)
            latch.countDown();
    }
}
