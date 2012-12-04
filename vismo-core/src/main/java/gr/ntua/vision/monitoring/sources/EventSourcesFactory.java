package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import org.zeromq.ZContext;


/**
 * 
 */
public class EventSourcesFactory {
    /***/
    private final VismoConfiguration conf;
    /***/
    private final ZMQSockets         zmq;


    /**
     * Constructor.
     * 
     * @param conf
     */
    public EventSourcesFactory(final VismoConfiguration conf) {
        this(conf, new ZMQSockets(new ZContext()));
    }


    /**
     * Constructor.
     * 
     * @param conf
     * @param zmq
     */
    public EventSourcesFactory(final VismoConfiguration conf, final ZMQSockets zmq) {
        this.conf = conf;
        this.zmq = zmq;
    }


    /**
     * Setup the sources group for cluster head.
     * 
     * @return an {@link EventSources} object.
     */
    public EventSources createforClusterHead() {
        return new EventSources().append(localSource()).append(sourceforAddress(withPort(conf.getClusterHeadPort())));
    }


    /**
     * Setup the sources group for worker.
     * 
     * @return an {@link EventSources} object.
     */
    public EventSources createforWorker() {
        return new EventSources().append(localSource());
    }


    /**
     * @return the localhost source.
     */
    private VismoEventSource localSource() {
        return sourceforAddress(conf.getProducersPoint());
    }


    /**
     * @param address
     * @return the event source for given address
     */
    private VismoEventSource sourceforAddress(final String address) {
        return new VismoEventSource(zmq.newBoundPullSocket(address), zmq.newConnectedPushSocket(address));
    }


    /**
     * @param port
     * @return
     */
    private static String withPort(final int port) {
        return "tcp://*:" + port;
    }
}
