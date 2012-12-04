package gr.ntua.vision.monitoring.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class UDPServer extends Thread {
    /***/
    private static final String          KILL      = "stop!";
    /** the log target. */
    private static final Logger          log       = LoggerFactory.getLogger(UDPServer.class);
    /***/
    private static final String          STATUS    = "status?";
    /***/
    private final ArrayList<UDPListener> listeners = new ArrayList<UDPListener>();
    /** the socket to use. */
    private final DatagramSocket         sock;


    /**
     * Constructor.
     * 
     * @param sock
     *            the socket to use.
     */
    UDPServer(final DatagramSocket sock) {
        super("udp-server");
        this.sock = sock;
        setDaemon(true);
        log.info("listening on port={}", sock.getLocalPort());
    }


    /**
     * @param listener
     */
    public void add(final UDPListener listener) {
        listeners.add(listener);
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

                if (KILL.equals(msg)) {
                    doKill();
                    send(KILL, pack);
                    break;
                }
                if (STATUS.equals(msg)) {
                    final ArrayList<String> statuses = new ArrayList<String>();

                    doStatus(statuses);

                    final String response = join(statuses, ", ");
                    send(response, pack);
                }
            } catch (final IOException e) {
                log.error("while receiving", e);
            }

        log.debug("shutting down");
    }


    /**
     * 
     */
    private void doKill() {
        for (final UDPListener listener : listeners)
            listener.halt();
    }


    /**
     * @param statuses
     */
    private void doStatus(final ArrayList<String> statuses) {
        for (final UDPListener listener : listeners)
            listener.collectStatus(statuses);
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
     * @param pack
     *            the datagram packet received.
     * @throws IOException
     */
    private void send(final String payload, final DatagramPacket pack) throws IOException {
        final byte[] buf = payload.getBytes();
        final DatagramPacket res = new DatagramPacket(buf, buf.length, pack.getAddress(), pack.getPort());

        sock.send(res);
    }


    /**
     * @param list
     * @param sep
     * @return the string concatenation of the strings, separated by the separator.
     */
    private static String join(final ArrayList<String> list, final String sep) {
        if (list.size() == 0)
            return "";
        if (list.size() == 1)
            return list.get(0);

        final StringBuilder buf = new StringBuilder();

        for (int i = 0; i < list.size() - 1; ++i)
            buf.append(list.get(i)).append(sep);

        buf.append(list.get(list.size() - 1));

        return buf.toString();
    }
}
