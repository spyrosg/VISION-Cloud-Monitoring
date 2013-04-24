package gr.ntua.vision.monitoring.zmq;

import gr.ntua.vision.monitoring.sockets.Socket;

import org.zeromq.ZMQ;


/**
 * 
 */
class ZMQSocket implements Socket {
    /** the address used. */
    private final String     addr;
    /** the actual zmq socket. */
    private final ZMQ.Socket sock;


    /**
     * Constructor.
     * 
     * @param sock
     *            the actual zmq socket.
     * @param addr
     *            the address used.
     */
    ZMQSocket(final ZMQ.Socket sock, final String addr) {
        this.sock = sock;
        this.addr = addr;
    }


    /**
     * @see gr.ntua.vision.monitoring.sockets.Socket#close()
     */
    @Override
    public void close() {
        sock.close();
    }


    /**
     * @see gr.ntua.vision.monitoring.sockets.Socket#isZMQPUB()
     */
    @Override
    public boolean isZMQPUB() {
        return sock.getType() == ZMQ.PUB;
    }


    /**
     * @see gr.ntua.vision.monitoring.sockets.Socket#receive()
     */
    @Override
    public String receive() {
        return recv(0);
    }


    /**
     * @see gr.ntua.vision.monitoring.sockets.Socket#receiveNonBlocking()
     */
    @Override
    public String receiveNonBlocking() {
        return recv(ZMQ.NOBLOCK);
    }


    /**
     * @see gr.ntua.vision.monitoring.sockets.Socket#send(java.lang.String)
     */
    @Override
    public boolean send(final String message) {
        return sock.send(message.getBytes(), 0);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<ZSocket: " + getType(sock) + ", port=" + addr + ">";
    }


    /**
     * @param flags
     * @return the message received, as a string, or, <code>null</code> on error or the socket has no available messages.
     */
    private String recv(final int flags) {
        final byte[] message = sock.recv(flags);

        if (message == null)
            return null;

        return new String(message, 0, message.length);
    }


    /**
     * @param sock
     *            the socket.
     * @return the socket type.
     */
    private static String getType(final ZMQ.Socket sock) {
        switch(sock.getType()){
            case 0:
                return "PAIR";
            case 1:
                return "PUB";
            case 2:
                return "SUB";
            case 3:
                return "REQ";
            case 4:
                return "REP";
            case 5:
                return "DEALER";
            case 6:
                return "ROUTER";
            case 7:
                return "PULL";
            case 8:
                return "PUSH";
            case 9:
                return "XPUB";
            case 10:
                return "XUSB";
            default:
                throw new UnsupportedOperationException("unknown zmq socket type: " + sock.getType());
        }
    }
}
