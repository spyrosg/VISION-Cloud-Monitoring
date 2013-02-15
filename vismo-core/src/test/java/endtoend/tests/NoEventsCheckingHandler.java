package endtoend.tests;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;


/**
 *
 */
abstract class NoEventsCheckingHandler implements EventHandler {
    /***/
    private int noReceivedEvents = 0;


    /**
     * Constructor.
     */
    public NoEventsCheckingHandler() {
    }


    /**
     * @param noExpectedEvents
     */
    public void haveReceivedExpectedNoEvents(final int noExpectedEvents) {
        assertEquals(noExpectedEvents, noReceivedEvents);
    }


    /**
     * @param e
     */
    protected void receivedEvent(@SuppressWarnings("unused") final MonitoringEvent e) {
        ++noReceivedEvents;
    }
}
