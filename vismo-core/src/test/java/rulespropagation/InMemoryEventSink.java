package rulespropagation;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.sinks.EventSink;

import java.util.ArrayList;


/**
 * 
 */
class InMemoryEventSink implements EventSink {
    /***/
    private final ArrayList<Event> eventStore;


    /**
     * Constructor.
     * 
     * @param eventStore
     */
    public InMemoryEventSink(final ArrayList<Event> eventStore) {
        this.eventStore = eventStore;
    }


    /**
     * @see gr.ntua.vision.monitoring.sinks.EventSink#send(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void send(final Event e) {
        eventStore.add(e);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<InMemoryEventSink: " + eventStore + ">";
    }
}
