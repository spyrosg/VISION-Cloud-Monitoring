package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.events.Event;

import java.util.Arrays;
import java.util.List;


/**
 * This provides a grouping for all event sinks used by system.
 */
public class EventSinks {
    /** the set of sinks. */
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
     * Push the event to all sinks.
     * 
     * @param e
     *            the event.
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
