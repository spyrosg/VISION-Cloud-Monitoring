package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.sinks.EventSink;

import java.util.Arrays;
import java.util.List;


/**
 * 
 */
public class EventSinks {
    /***/
    private final List<EventSink> sinks;


    /**
     * Constructor.
     * 
     * @param sinks
     */
    public EventSinks(final EventSink... sinks) {
        this(Arrays.asList(sinks));
    }


    /**
     * Constructor.
     * 
     * @param sinks
     */
    public EventSinks(final List<EventSink> sinks) {
        this.sinks = sinks;
    }


    /**
     * @param e
     */
    public void push(final Event e) {
        for (final EventSink sink : sinks)
            sink.send(e);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<EventSinks: " + sinks + ">";
    }
}
