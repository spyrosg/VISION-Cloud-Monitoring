package gr.ntua.vision.monitoring.zmq;

import gr.ntua.vision.monitoring.MonitoringSocket;

import org.zeromq.ZMQ.Socket;


/**
 * 
 */
public class VismoSocket implements MonitoringSocket {
    /** the address used. */
    private final String addr;
    /** the actual zmq socket. */
    private final Socket sock;


    /**
     * Constructor.
     * 
     * @param sock
     *            the actual zmq socket.
     * @param addr
     *            the address used.
     */
    VismoSocket(final Socket sock, final String addr) {
        this.sock = sock;
        this.addr = addr;
    }


    /**
     * Close the socket.
     */
    @Override
    public void close() {
        sock.close();
    }


    /**
     * @return the message received, or <code>null</code> on io error.
     */
    @Override
    public String receive() {
        final byte[] message = sock.recv(0);

        if (message == null)
            return null;

        return new String(message, 0, message.length);
    }


    /**
     * Send the message.
     * 
     * @param message
     *            the message to send.
     * @return <code>true</code> on success, <code>false</code> otherwise.
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
     * @param sock
     *            the socket.
     * @return the socket type.
     */
    private static String getType(final Socket sock) {
        switch (sock.getType()){
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
