package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.sinks.BasicEventSink;
import gr.ntua.vision.monitoring.sources.BasicEventSource;
import gr.ntua.vision.monitoring.sources.EventSource;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A worker's responsibility is to pass events received from localhost to the cluster head.
 */
public class VismoWorkerNode extends AbstractVismoCloudElement {
    /***/
    private static final Logger      log = LoggerFactory.getLogger(VismoWorkerNode.class);
    /***/
    @SuppressWarnings("unused")
    private PassThroughEventListener listener;


    /**
     * Constructor.
     * 
     * @param service
     */
    public VismoWorkerNode(final VismoService service) {
        super(service);
    }


    /**
     * Setup a source from localhost, and a sink to the cluster head.
     * 
     * @see gr.ntua.vision.monitoring.VismoCloudElement#setup(gr.ntua.vision.monitoring.VismoConfiguration,
     *      gr.ntua.vision.monitoring.zmq.ZMQSockets)
     */
    @Override
    public void setup(final VismoConfiguration conf, final ZMQSockets zmq) {
        final BasicEventSource source = new BasicEventSource(new VismoEventFactory(), zmq.newBoundPullSocket("tcp://127.0.0.1:"
                + conf.getProducersPort()));

        attach(source);

        final BasicEventSink sink = new BasicEventSink(zmq.newConnectedPushSocket("tcp://" + conf.getClusterHead() + ":"
                + conf.getClusterHeadPort()));

        attach(sink);

        listener = new PassThroughEventListener(source, sink);
    }


    /**
     * @see gr.ntua.vision.monitoring.AbstractVismoCloudElement#log()
     */
    @Override
    protected Logger log() {
        return log;
    }


    /**
     * @see gr.ntua.vision.monitoring.VismoCloudElement#start()
     */
    @Override
    public void start() {
        for (final EventSource source : sources)
            service.addTask((BasicEventSource) source);
    }
}
