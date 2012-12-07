package gr.ntua.vision.monitoring.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to send commands to the server. Supported commands are <code>kill</code> (to shut down a running vismo instance)
 * and <code>status</code> (to get back a status report from a running instance).
 */
public class UDPClient {
    /***/
    private static final String  KILL    = "stop!";
    /** the log target. */
    private static final Logger  log     = LoggerFactory.getLogger(UDPClient.class);
    /***/
    private static final String  STATUS  = "status?";
    /***/
    private static final int     TIMEOUT = 1000;
    /** the port to use. */
    private final int            port;
    /** the sock to use. */
    private final DatagramSocket sock;


    /**
     * Constructor.
     * 
     * @param port
     *            the port to use.
     * @throws SocketException
     */
    UDPClient(final int port) throws SocketException {
        this.sock = new DatagramSocket();
        this.sock.setSoTimeout(TIMEOUT);
        this.port = port;
        log.debug("upd client will connect to port={}", port);
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
