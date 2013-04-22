package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.sockets.Socket;


/**
 * This is providing utility methods for transmitting an event through a socket.
 */
abstract class AbstractSink implements EventSink {
    /***/
    private volatile boolean closed = false;
    /***/
    private final Socket     sock;


    /**
     * Constructor.
     * 
     * @param sock
     */
    public AbstractSink(final Socket sock) {
        this.sock = sock;
    }


    /**
     * @see gr.ntua.vision.monitoring.sinks.EventSink#close()
     */
    @Override
    public void close() {
        sock.close();
        closed = true;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<" + getClass().getSimpleName() + ": " + sock + ">";
    }


    /**
     * Transmit the string through the socket.
     * 
     * @param str
     *            a string.
     */
    protected void send(final String str) {
        if (closed)
            return;

        sock.send(str);
    }
}
