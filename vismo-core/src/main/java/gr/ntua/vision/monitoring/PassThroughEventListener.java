package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sources.EventSource;


/**
 *
 */
public class PassThroughEventListener implements EventListener {
    /***/
    private final EventSink sink;


    /**
     * Constructor.
     * 
     * @param source
     * @param sink
     */
    public PassThroughEventListener(EventSource source, EventSink sink) {
        source.subscribe(this);
        this.sink = sink;
    }


    /**
     * @see gr.ntua.vision.monitoring.EventListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void receive(Event e) {
        sink.send(e);
    }
}
