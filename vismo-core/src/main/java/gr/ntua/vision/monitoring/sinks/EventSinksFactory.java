package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import org.zeromq.ZContext;


/**
 * 
 */
public class EventSinksFactory {
    /***/
    private final VismoConfiguration conf;
    /***/
    private final ZMQSockets         zmq;


    /**
     * Constructor.
     * 
     * @param conf
     */
    public EventSinksFactory(final VismoConfiguration conf) {
        this(conf, new ZMQSockets(new ZContext()));
    }


    /**
     * Constructor.
     * 
     * @param conf
     * @param zmq
     */
    public EventSinksFactory(final VismoConfiguration conf, final ZMQSockets zmq) {
        this.conf = conf;
        this.zmq = zmq;
    }


    /**
     * Setup the sinks for a cluster head node.
     * 
     * @return an {@link EventSinks} object.
     */
    public EventSinks createForClusterHead() {
        final EventSink sink = new UniqueEventSink(zmq.newBoundPubSocket("tcp://*:" + conf.getConsumersPort()));
        final EventSink cloudSink = new UniqueEventSink(zmq.newConnectedPushSocket("tcp://" + conf.getCloudHeads().get(0) + ":"
                + conf.getCloudHeadPort()));

        return new EventSinks(sink, cloudSink);
    }


    /**
     * Setup the sinks for a worker node.
     * 
     * @return an {@link EventSinks} object.
     */
    public EventSinks createForWorker() {
        return new EventSinks(new UniqueEventSink(zmq.newConnectedPushSocket("tcp://" + conf.getClusterHead() + ":"
                + conf.getClusterHeadPort())));
    }
}
