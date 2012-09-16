package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.zmq.ZMQSockets;


/**
 *
 */
public class VismoCloudElementFactory {
    /***/
    private final VismoConfiguration      conf;
    /***/
    private final VismoEventSourceFactory eventFactory;
    /***/
    private final ZMQSockets              zmq;


    /**
     * @param eventFactory
     * @param zmq
     * @param conf
     */
    private VismoCloudElementFactory(VismoEventSourceFactory eventFactory, final ZMQSockets zmq, final VismoConfiguration conf) {
        this.zmq = zmq;
        this.conf = conf;
        this.eventFactory = eventFactory;

    }


    /**
     * @return
     */
    public VismoClusterNode createVismoClusterNode() {
        // TODO
        return null;
    }


    /**
     * @return
     */
    public VismoNode createVismoNode() {
        VismoEventSource source = eventFactory.create(zmq);

        return new VismoNode(source, sink);
    }
}
