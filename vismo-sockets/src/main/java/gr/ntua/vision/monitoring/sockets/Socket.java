package gr.ntua.vision.monitoring.sockets;

/**
 * A socket is used to send and receive messages to/from a network address.
 */
public interface Socket {
    /**
     * Close the socket.
     */
    void close();


    /**
     * @return the message received, or <code>null</code> on io error.
     */
    String receive();


    /**
     * Send the message.
     * 
     * @param message
     *            the message to send.
     * @return <code>true</code> on success, <code>false</code> otherwise.
     */
    boolean send(String message);

    /**
     * @return
     */
    boolean isZMQPUB();
}
