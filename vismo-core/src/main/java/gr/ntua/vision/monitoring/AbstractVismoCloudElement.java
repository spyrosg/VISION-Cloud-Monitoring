package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sources.BasicEventSource;
import gr.ntua.vision.monitoring.sources.EventSource;
import gr.ntua.vision.monitoring.udp.UDPListener;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.zeromq.ZMQ;


/**
 * 
 */
abstract class AbstractVismoCloudElement implements VismoCloudElement, EventListener, UDPListener {
    /***/
    private static final String            KILL   = "stop!";
    /***/
    private static final String            STATUS = "status?";
    /***/
    protected final EventSink              sink;
    /***/
    protected final List<BasicEventSource> sources;
    /** the list of supporting tasks. */
    private final ArrayList<StoppableTask> tasks  = new ArrayList<StoppableTask>();
    /***/
    private final VMInfo                   vminfo;


    /**
     * Constructor.
     * 
     * @param vminfo
     * @param sink
     * @param sources
     * @throws SocketException
     */
    public AbstractVismoCloudElement(final VMInfo vminfo, final EventSink sink, final BasicEventSource... sources)
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
    public AbstractVismoCloudElement(final VMInfo vminfo, final EventSink sink, final List<BasicEventSource> sources)
            throws SocketException {
        this.vminfo = vminfo;
        this.sink = sink;
        this.sources = sources;
        logStartup();
    }


    /**
     * Prepare the task to run.
     * 
     * @param t
     *            the task.
     */
    public void addTask(final StoppableTask t) {
        log().debug("adding slave task {}", t);
        tasks.add(t);
    }


    /**
     * @see gr.ntua.vision.monitoring.udp.UDPListener#notify(java.lang.String)
     */
    @Override
    public String notify(final String msg) {
        if (msg.equals(STATUS))
            return status();

        stop();
        return KILL;
    }


    /**
     * @see gr.ntua.vision.monitoring.VismoCloudElement#start()
     */
    @Override
    public void start() {
        for (final EventSource source : sources)
            source.subscribe(this);

        log().debug("starting {} tasks", tasks.size());

        for (final Thread t : tasks)
            t.start();

        // log().debug("scheduling {} timer tasks", tasks.size());
        // timer.start();
    }


    /**
     * @return the logger object.
     */
    protected abstract Logger log();


    /**
     * @param e
     */
    protected void send(final Event e) {
        sink.send(e);
    }


    /**
     * @return the jvm's pid.
     */
    protected String status() {
        return String.valueOf(vminfo.getPID());
    }


    /**
     * 
     */
    protected void stop() {
        log().info("shutting down");

        for (final StoppableTask t : tasks)
            try {
                t.shutDown();
            } catch (final Throwable x) {
                log().error("exception while shutting down", x);
            }

        log().debug("shutdown completed normally.");
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
