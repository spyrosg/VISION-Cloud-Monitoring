package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.sinks.BasicEventSink;
import gr.ntua.vision.monitoring.sources.BasicEventSource;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A worker's responsibility is to pass events received from localhost to the cluster head.
 */
public class VismoWorkerNode implements VismoCloudElement {
    /***/
    private static final Logger      log = LoggerFactory.getLogger(VismoWorkerNode.class);
    /***/
    private final VismoConfiguration conf;
    /***/
    private final VismoService       service;
    /***/
    private final ZMQSockets         zmq;


    /**
     * Constructor.
     * 
     * @param service
     * @param conf
     * @param zmq
     */
    public VismoWorkerNode(final VismoService service, final VismoConfiguration conf, final ZMQSockets zmq) {
        this.service = service;
        this.conf = conf;
        this.zmq = zmq;
    }


    /**
     * @see gr.ntua.vision.monitoring.VismoCloudElement#setup()
     */
    @Override
    public void setup() {
        log.debug("setting up");

        final BasicEventSource source = new BasicEventSource(new VismoEventFactory(), zmq.newBoundPullSocket(conf
                .getProducersPoint()));
        final BasicEventSink sink = new BasicEventSink(zmq.newConnectedPushSocket("tcp://" + conf.getClusterHead() + ":"
                + conf.getClusterHeadPort()));

        source.subscribe(new EventListener() {
            @Override
            public void receive(final Event e) {
                sink.send(e);
            }
        });
        service.addTask(source);
    }
}
