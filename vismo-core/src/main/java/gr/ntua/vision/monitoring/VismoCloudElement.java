package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.zmq.ZMQSockets;

/**
 *
 */
public interface VismoCloudElement {
    /**
     * 
     */
    void start();

    /**
     * @param conf
     * @param zmq
     */
    void setup(VismoConfiguration conf, ZMQSockets zmq);
}
