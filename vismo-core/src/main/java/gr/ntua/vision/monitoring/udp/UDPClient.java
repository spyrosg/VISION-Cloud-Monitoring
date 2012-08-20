package gr.ntua.vision.monitoring.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public class UDPClient {
    /***/
    private static final String  KILL    = "stop!";
    /***/
    private static final String  STATUS  = "status?";
    /** the response timeout. */
    private static final int     TIMEOUT = (int) TimeUnit.SECONDS.toMillis(1);
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
    public String getVismoStatus() throws IOException {
        return sendMessage(STATUS);
    }


    /**
     * Tell listener to shutdown.
     * 
     * @throws IOException
     */
    public void shutdownVismo() throws IOException {
        sendMessage(KILL);
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


    /**
     * @param command
     * @return the message received from the server.
     * @throws IOException
     */
    private String sendMessage(final String command) throws IOException {
        send(command);
        return receive();
    }
}
