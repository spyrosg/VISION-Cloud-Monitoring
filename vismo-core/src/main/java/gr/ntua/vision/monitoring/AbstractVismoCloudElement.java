package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sources.EventSource;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.util.ArrayList;

import org.slf4j.Logger;


/**
 * 
 */
abstract class AbstractVismoCloudElement implements VismoCloudElement {
    /***/
    protected final VismoService           service;
    /***/
    protected final ArrayList<EventSink>   sinks   = new ArrayList<EventSink>();
    /***/
    protected final ArrayList<EventSource> sources = new ArrayList<EventSource>();
    /***/
    private final VismoEventWorker         worker;


    /**
     * Constructor.
     * 
     * @param service
     * @param worker
     */
    public AbstractVismoCloudElement(final VismoService service, final VismoEventWorker worker) {
        this.service = service;
        this.worker = worker;
    }


    /**
     * @see gr.ntua.vision.monitoring.VismoCloudElement#setup(gr.ntua.vision.monitoring.VismoConfiguration,
     *      gr.ntua.vision.monitoring.zmq.ZMQSockets)
     */
    @Override
    public void setup(VismoConfiguration conf, ZMQSockets zmq) {
        for (final EventSource source : sources)
            source.subscribe(worker);
    }


    public void foo() {
        final Event e = worker.getWork();

        for (final EventSink sink : sinks)
            sink.send(e);
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
     * @return the logger object.
     */
    protected abstract Logger log();
}
