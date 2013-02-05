package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.ArrayList;


/**
 * @author tmessini
 */
public class InMemoryEventSource implements EventSource {
    /***/
    private final ArrayList<EventSourceListener> listeners = new ArrayList<EventSourceListener>();


    /**
     * Constructor.
     */
    public InMemoryEventSource() {
        // NOP
    }


    /**
     * @see gr.ntua.vision.monitoring.sources.EventSource#add(gr.ntua.vision.monitoring.sources.EventSourceListener)
     */
    @Override
    public void add(final EventSourceListener listener) {
        listeners.add(listener);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<InMemoryEventSource>";
    }


    /**
     * @param e
     */
    public void triggerRuleEvaluationWith(final MonitoringEvent e) {
        for (final EventSourceListener listener : listeners)
            listener.receive(e);
    }
}
