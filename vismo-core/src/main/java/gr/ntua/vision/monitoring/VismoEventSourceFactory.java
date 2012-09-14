package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;


/**
 *
 */
public class VismoEventSourceFactory {
    /** the configuration object. */
    private final VismoConfiguration config;


    /**
     * Constructor.
     * 
     * @param config
     *            the configuration object.
     */
    public VismoEventSourceFactory(final VismoConfiguration config) {
        this.config = config;
    }


    /**
     * @param zmq
     * @return
     */
    public VismoEventSource build(final ZMQSockets zmq) {
        return new VismoEventSource(zmq.newBoundPullSocket(config.getProducersPoint()), zmq.newConnectedPushSocket(config
                .getProducersPoint()), new VismoEventFactory());
    }
}
