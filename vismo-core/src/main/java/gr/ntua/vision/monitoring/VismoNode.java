package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;


/**
 *
 */
public class VismoNode implements VismoCloudElement, EventListener {
    /***/
    private final EventSource source;
    /***/
    private final EventSink   sink;


    /**
     * Constructor.
     * 
     * @param source
     * @param sink
     */
    public VismoNode(EventSource source, EventSink sink) {
        this.source = source;
        this.sink = sink;
    }


    /**
     * 
     */
    @Override
    public void start() {
        source.subscribe(this);
    }


    /**
     * @see gr.ntua.vision.monitoring.EventListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void receive(Event e) {
        doYourThing(e);
    }


    /**
     * @param e
     */
    private void doYourThing(final Event e) {
        // TODO: maybe in another thread?
        sink.send(e);
    }
}
