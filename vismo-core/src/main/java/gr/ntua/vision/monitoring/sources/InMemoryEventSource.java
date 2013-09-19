package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 * 
 */
public class InMemoryEventSource implements EventSource {
    /***/
    private final EventSourceListener listener;


    /**
     * Constructor.
     * 
     * @param listener
     */
    public InMemoryEventSource(final EventSourceListener listener) {
        this.listener = listener;
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
        listener.receive(e);
    }
}
