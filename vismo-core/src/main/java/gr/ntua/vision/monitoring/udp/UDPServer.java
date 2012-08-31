package gr.ntua.vision.monitoring.udp;

import gr.ntua.vision.monitoring.StoppableTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class UDPServer extends StoppableTask {
    /** the log target. */
    private static final Logger  log = LoggerFactory.getLogger(UDPServer.class);
    /** the listener to notify. */
    private final UDPListener    listener;
    /** the socket to use. */
    private final DatagramSocket sock;


    /**
     * Constructor.
     * 
     * @param sock
     *            the socket to use.
     * @param listener
     *            the listener to notify.
     */
    UDPServer(final DatagramSocket sock, final UDPListener listener) {
        super("udp-server");
        this.sock = sock;
        this.listener = listener;
        log.info("listening on port={}", sock.getLocalPort());
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        log.debug("entering receive/reply loop");

        while (!isInterrupted())
            try {
                final DatagramPacket pack = receive();
                final String msg = new String(pack.getData(), 0, pack.getLength());

                log.debug("received: {}", msg);
                send(listener.notify(msg), pack.getAddress(), pack.getPort());
            } catch (final IOException e) {
                log.error("while receiving", e);
            }

        log.debug("shutting down");
    }


    /**
     * @see gr.ntua.vision.monitoring.StoppableTask#shutDown()
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
