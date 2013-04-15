package gr.ntua.monitoring.sockets;

/**
 *
 */
public interface SocketFactory {
    /**
     * Return a bound pull socket.
     * 
     * @param addr
     *            the address to bind to.
     * @return a bound to the address pull socket.
     */
    Socket newBoundPullSocket(String addr);


    /**
     * Return a bound push socket.
     * 
     * @param addr
     *            the address to bind to.
     * @return a bound to the address pull socket.
     */
    Socket newBoundPushSocket(String addr);


    /**
     * Return a bound pull socket.
     * 
     * @param addr
     *            the address to connect to.
     * @return a connected to the address push socket.
     */
    Socket newConnectedPullSocket(String addr);


    /**
     * Return a connected push socket.
     * 
     * @param addr
     *            the address to connect to.
     * @return a connected to the address push socket.
     */
    Socket newConnectedPushSocket(String addr);


    /**
     * Return a connected publish socket.
     * 
     * @param addr
     *            the address to bind to.
     * @return a new connected publish socket.
     */
    Socket newPubConnectSocket(String addr);


    /**
     * Return a bound publish socket.
     * 
     * @param addr
     *            the address to bind to.
     * @return a new bound publish socket.
     */
    Socket newPubSocket(String addr);


    /**
     * Return a connected subscribed socket.
     * 
     * @param addr
     *            the address to connect to.
     * @param topic
     *            the topic to subscribe to.
     * @return a connected socket, subscribed to the given topic.
     */
    Socket newSubSocket(String addr, String topic);
}
