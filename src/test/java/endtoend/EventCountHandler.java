package endtoend;

import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventHandler;


/**
 * This is used to verify that the {@link FakeEventConsumer} receives the expected number of events from the main monitoring
 * instance, during the test.
 */
class EventCountHandler implements EventHandler {
    /***/
    private final int noExpectedEvents;
    /***/
    private int       noReceivedEvents = 0;


    /**
     * @param noExpectedEvents
     */
    public EventCountHandler(final int noExpectedEvents) {
        this.noExpectedEvents = noExpectedEvents;
    }


    /**
     * @see gr.ntua.vision.monitoring.events.EventHandler#handler(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void handler(final Event e) {
        ++noReceivedEvents;
    }


    /***/
    public void haveReceivedEnoughMessages() {
        assertTrue("not enough events received: " + noReceivedEvents, noReceivedEvents >= noExpectedEvents);
    }
}
