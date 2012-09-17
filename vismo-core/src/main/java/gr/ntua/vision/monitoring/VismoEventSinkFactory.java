package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.zmq.ZMQSockets;


/**
 * 
 */
public class VismoEventSinkFactory {
    /***/
    private final ZMQSockets zmq;


    /**
     * Constructor.
     * 
     * @param zmq
     */
    public VismoEventSinkFactory(final ZMQSockets zmq) {
        this.zmq = zmq;
    }


    /**
     * @return
     */
    public EventSink create(final String address) {
        return null;
    }
}
