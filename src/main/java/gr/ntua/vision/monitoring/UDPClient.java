package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;


/**
 *
 */
public class UDPClient {
    /** the time to wait for a response. */
    private static final int     TIMEOUT = 1000;
    /** the port to send messages to. */
    private final int            port;
    /** the socket. */
    private final DatagramSocket sock;


    /**
     * Constructor.
     * 
     * @param port
     *            the port to send messages to.
     * @throws SocketException
     */
    public UDPClient(final int port) throws SocketException {
        this.port = port;
        this.sock = new DatagramSocket();
        this.sock.setSoTimeout(TIMEOUT);
    }


    /**
     * Ask the server for its status.
     * 
     * @return the server's response.
     * @throws SocketTimeoutException
     *             when the server is down.
     * @throws IOException
     */
    public String requestStatus() throws IOException {
        return sendMessage("status");
    }


    /**
     * Ask the server to shutdown.
     * 
     * @throws IOException
     */
    public void requestStop() throws IOException {
        sendMessage("stop");
    }


    /**
     * Receive a datagram packet.
     * 
     * @return the payload of the datagram received.
     * @throws IOException
     */
    private String receive() throws IOException {
        final byte[] buf = new byte[64];
        final DatagramPacket req = new DatagramPacket(buf, buf.length);

        sock.receive(req);

        return new String(req.getData(), 0, req.getLength());
    }


    /**
     * @param command
     * @return
     * @throws IOException
     */
    private String sendMessage(final String command) throws IOException {
        send(command);
        return receive();
    }


    /**
     * Send as a datagram packet the given content.
     * 
     * @param payload
     *            the payload to send.
     * @throws IOException
     */
    private void send(final String payload) throws IOException {
        final byte[] buf = payload.getBytes();
        final DatagramPacket req = new DatagramPacket(buf, buf.length, InetAddress.getLocalHost(), port);

        sock.send(req);
    }
}
