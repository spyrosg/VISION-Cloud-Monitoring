package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.zmq.ZMQSockets;


/**
 *
 */
public class LocalEventsCollectorFactory {
    /** the configuration object. */
    private final VismoConfiguration config;


    /**
     * Constructor.
     * 
     * @param config
     *            the configuration object.
     */
    public LocalEventsCollectorFactory(final VismoConfiguration config) {
        this.config = config;
    }


    /**
     * @param zmq
     * @return a setup {@link LocalEventsCollector}.
     */
    public LocalEventsCollector build(final ZMQSockets zmq) {
        return new LocalEventsCollector(zmq.newBoundPullSocket(config.getProducersPoint()), zmq.newConnectedPushSocket(config
                .getProducersPoint()));
    }
}
