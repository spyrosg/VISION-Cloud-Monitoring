package helpers;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.sinks.EventSink;

import java.util.ArrayList;


/**
 * 
 */
public class InMemoryEventSink implements EventSink {
    /***/
    private final ArrayList<MonitoringEvent> eventStore;


    /**
     * Constructor.
     * 
     * @param eventStore
     */
    public InMemoryEventSink(final ArrayList<MonitoringEvent> eventStore) {
        this.eventStore = eventStore;
    }


    /**
     * @see gr.ntua.vision.monitoring.sinks.EventSink#send(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void send(final MonitoringEvent e) {
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
