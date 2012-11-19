package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sources.EventSource;

import java.util.List;


/**
 * 
 */
public class EventChannel implements EventSourceListener {
    /***/
    private final List<EventSink> sinks;


    /**
     * Constructor.
     * 
     * @param sinks
     */
    public EventChannel(final List<EventSink> sinks) {
        this.sinks = sinks;
    }


    /**
     * @param source
     */
    public void addSource(final EventSource source) {
        source.add(this);
    }


    /**
     * @see gr.ntua.vision.monitoring.EventSourceListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void receive(final Event e) {
        for (final EventSink sink : sinks)
            sink.send(e);
    }
}
