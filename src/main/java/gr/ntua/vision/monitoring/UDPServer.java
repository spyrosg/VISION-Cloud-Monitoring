package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class UDPServer extends MonitoringTask {
    /***/
    private final UDPListener    listener;
    /** the log target. */
    private final Logger         log = LoggerFactory.getLogger(getClass());
    /***/
    private final DatagramSocket sock;


    /**
     * @param port
     * @param listener
     * @throws SocketException
     */
    public UDPServer(final int port, final UDPListener listener) throws SocketException {
        super("udp-server");
        this.sock = new DatagramSocket(port);
        this.sock.setReuseAddress(true);
        this.listener = listener;
        log.info("upd server started on port={}", port);
    }


    @Override
    public void run() {
        while (!isInterrupted())
            try {
                final DatagramPacket pack = receive();
                final String msg = new String(pack.getData(), 0, pack.getLength());

                log.debug("received '{}'", msg);
                send(listener.notify(msg), pack.getAddress(), pack.getPort());
            } catch (final IOException e) {
                log.error("while receiving", e);
            }
    }


    /**
     * @see gr.ntua.vision.monitoring.MonitoringTask#shutDown()
     */
    @Override
    public void shutDown() {
        interrupt();
    }


    /**
     * Receive a datagram packet from the socket.
     * 
     * @return a {@link DatagramPacket}.
     * @throws IOException
     */
    private DatagramPacket receive() throws IOException {
        final byte[] buf = new byte[64];
        final DatagramPacket req = new DatagramPacket(buf, buf.length);

        sock.receive(req);

        return req;
    }


    /**
     * Send as a datagram packet the given content.
     * 
     * @param payload
     *            the payload to send.
     * @param addr
     *            the address to sent to.
     * @param port
     *            the port to sent to.
     * @throws IOException
     */
    private void send(final String payload, final InetAddress addr, final int port) throws IOException {
        final byte[] buf = payload.getBytes();
        final DatagramPacket res = new DatagramPacket(buf, buf.length, addr, port);

        sock.send(res);
    }
}
