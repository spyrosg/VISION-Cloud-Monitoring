package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.scheduling.VismoRepeatedTask;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sources.EventSource;

import java.util.ArrayList;

import org.slf4j.Logger;


/**
 * 
 */
abstract class AbstractVismoCloudElement implements VismoCloudElement {
    /***/
    protected final ArrayList<EventSink>   sinks   = new ArrayList<EventSink>();
    /***/
    protected final ArrayList<EventSource> sources = new ArrayList<EventSource>();
    /***/
    protected final VismoService           service;


    /**
     * Constructor.
     * 
     * @param service
     */
    public AbstractVismoCloudElement(final VismoService service) {
        this.service = service;
    }


    /**
     * @param sink
     */
    public void attach(final EventSink sink) {
        log().debug("attaching {}", sink);
        sinks.add(sink);
    }


    /**
     * @param source
     */
    public void attach(final EventSource source) {
        log().debug("attaching {}", source);
        sources.add(source);
    }


    /**
     * @param task
     */
    protected void addTask(final VismoRepeatedTask task) {
        service.addTask(task);
    }


    /**
     * @return the logger object.
     */
    protected abstract Logger log();


    /**
     * @param e
     */
    protected void send(final Event e) {
        for (final EventSink sink : sinks)
            sink.send(e);
    }
}
