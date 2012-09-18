package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;

import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.zeromq.ZMQ;


/**
 * 
 */
abstract class AbstractVismoCloudElement implements VismoCloudElement, EventListener {
    /***/
    private final EventSink         sink;
    /***/
    private final List<EventSource> sources;
    /***/
    private final VMInfo            vminfo;


    /**
     * Constructor.
     * 
     * @param vminfo
     * @param sink
     * @param sources
     * @throws SocketException
     */
    public AbstractVismoCloudElement(final VMInfo vminfo, final EventSink sink, final EventSource... sources)
            throws SocketException {
        this(vminfo, sink, Arrays.asList(sources));
    }


    /**
     * Constructor.
     * 
     * @param vminfo
     * @param sink
     * @param sources
     * @throws SocketException
     */
    public AbstractVismoCloudElement(final VMInfo vminfo, final EventSink sink, final List<EventSource> sources)
            throws SocketException {
        this.vminfo = vminfo;
        this.sink = sink;
        this.sources = sources;
        logStartup();
    }


    /**
     * @see gr.ntua.vision.monitoring.VismoCloudElement#start()
     */
    @Override
    public void start() {
        for (final EventSource source : sources)
            source.subscribe(this);
    }


    /**
     * @return
     */
    protected abstract Logger log();


    /**
     * @param e
     */
    protected void send(final Event e) {
        sink.send(e);
    }


    /**
     * @throws SocketException
     */
    private void logStartup() throws SocketException {
        log().info("Starting up, pid={}, ip={}", vminfo.getPID(), vminfo.getInterface().getDisplayName() + vminfo.getAddress());
        log().info("running zmq version={}", ZMQ.getVersionString());
        log().debug("\twith sink: {}", sink);

        for (final EventSource source : sources)
            log().debug("\twith source: {}", source);
    }
}
