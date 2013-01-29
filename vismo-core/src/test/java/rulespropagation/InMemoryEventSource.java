package rulespropagation;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.sources.EventSource;
import gr.ntua.vision.monitoring.sources.EventSourceListener;

import java.util.ArrayList;


/**
 * @author tmessini
 */
class InMemoryEventSource implements EventSource {
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
    public void triggerRuleEvaluationWith(final Event e) {
        for (final EventSourceListener listener : listeners)
            listener.receive(e);
    }
}
