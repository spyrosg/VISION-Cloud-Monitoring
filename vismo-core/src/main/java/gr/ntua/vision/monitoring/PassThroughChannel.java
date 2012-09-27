package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.sinks.EventSink;


/**
 * 
 */
public class PassThroughChannel implements EventListener {
    /***/
    private final EventSink sink;


    /**
     * Constructor.
     * 
     * @param sink
     */
    public PassThroughChannel(final EventSink sink) {
        this.sink = sink;
    }


    /**
     * @see gr.ntua.vision.monitoring.EventListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void receive(final Event e) {
        sink.send(e);
    }
}
