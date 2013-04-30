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
     * @return <code>true</code> iff this is a zmq pub socket, <code>false</code> otherwise.
     */
    boolean isZMQPUB();


    /**
     * Block and wait for a message to receive.
     * 
     * @return the message received, or <code>null</code> on io error.
     */
    String receive();


    /**
     * If a message is available to read off the socket, deliver it. Else, return <code>null</code> without waiting.
     * 
     * @return the message received, or <code>null</code> on io error.
     */
    String receiveNonBlocking();


    /**
     * Send the message.
     * 
     * @param message
     *            the message to send.
     * @return <code>true</code> on success, <code>false</code> otherwise.
     */
    boolean send(String message);
}
