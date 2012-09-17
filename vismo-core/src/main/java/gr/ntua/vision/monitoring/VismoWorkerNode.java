package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;

import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;


/**
 *
 */
public class VismoWorkerNode implements VismoCloudElement, EventListener {
    /***/
    private static final Logger log = LoggerFactory.getLogger(VismoWorkerNode.class);
    /***/
    private final EventSink     sink;
    /***/
    private final EventSource   source;


    /**
     * Constructor.
     * 
     * @param vminfo
     * @param source
     * @param sink
     * @throws SocketException
     */
    public VismoWorkerNode(final VMInfo vminfo, final EventSource source, final EventSink sink) throws SocketException {
        this.source = source;
        this.sink = sink;
        log.info("Starting up, pid={}, ip={}", vminfo.getPID(), vminfo.getInterface().getDisplayName() + vminfo.getAddress());
        log.info("running zmq version={}", ZMQ.getVersionString());
        log.debug("\twith source: {}", source);
        log.debug("\twith sink: {}", sink);
    }


    /**
     * @see gr.ntua.vision.monitoring.EventListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void receive(final Event e) {
        doYourThing(e);
    }


    /**
     * 
     */
    @Override
    public void start() {
        source.subscribe(this);
    }


    /**
     * @param e
     */
    private void doYourThing(final Event e) {
        // TODO: maybe in another thread?
        sink.send(e);
    }
}
