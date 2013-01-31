package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;


/**
 * 
 */
public class InMemoryEventSink implements EventSink {
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
