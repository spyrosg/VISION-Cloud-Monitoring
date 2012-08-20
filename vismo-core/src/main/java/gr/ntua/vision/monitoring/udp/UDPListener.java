package gr.ntua.vision.monitoring.udp;

/**
 * This is used to notify any interested party for incoming udp requests.
 */
public interface UDPListener {
    /**
     * Pass to the listener the message received and report back a response.
     * 
     * @param msg
     *            the message received.
     * @return the listener's response.
     */
    String notify(String msg);
}
