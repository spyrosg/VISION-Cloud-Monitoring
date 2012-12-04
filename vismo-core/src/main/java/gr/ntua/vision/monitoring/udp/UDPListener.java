package gr.ntua.vision.monitoring.udp;

import java.util.List;


/**
 * This is used to notify any interested party for incoming udp requests.
 */
public interface UDPListener {
    /**
     * @param statuses
     */
    void collectStatus(final List<String> statuses);


    /**
     * 
     */
    void halt();
}
