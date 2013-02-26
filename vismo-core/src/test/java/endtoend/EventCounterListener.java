package endtoend;

import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.EventSourceListener;
import gr.ntua.vision.monitoring.events.Event;


/**
 * This is used to verify that the main monitoring instance receives the expected number of events from {@link FakeEventProducer},
 * during the test.
 */
class EventCounterListener implements EventSourceListener {
    /***/
    private final int noExpectedEvents;
    /***/
    private int       noReceivedEvents = 0;


    /**
     * @param noExpectedEvents
     */
    public EventCounterListener(final int noExpectedEvents) {
        this.noExpectedEvents = noExpectedEvents;
    }


    /***/
    public void haveReceivedEnoughMessages() {
        assertTrue("not enough events received: " + noReceivedEvents, noReceivedEvents >= noExpectedEvents);
    }


    /**
     * @see gr.ntua.vision.monitoring.EventSourceListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void receive(final Event e) {
        ++noReceivedEvents;
    }
}