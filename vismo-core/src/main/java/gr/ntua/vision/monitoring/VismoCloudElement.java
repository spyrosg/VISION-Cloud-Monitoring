package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.zmq.ZMQSockets;


/**
 *
 */
public interface VismoCloudElement {
    /**
     * @param conf
     * @param zmq
     */
    void setup(VismoConfiguration conf, ZMQSockets zmq);


    /**
     * 
     */
    void start();
}
