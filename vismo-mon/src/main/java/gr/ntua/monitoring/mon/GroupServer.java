package gr.ntua.monitoring.mon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class GroupServer implements Runnable {
    /** the log target. */
    private static final Logger      log = LoggerFactory.getLogger(GroupServer.class);
    /** the group address. */
    private final InetAddress        groupAddress;
    /** notifications received. */
    private final Collection<String> notifications;
    /** the group port. */
    private final int                port;


    /**
     * Constructor.
     * 
     * @param port
     *            the group port.
     * @param groupAddress
     *            the group address.
     * @param notifications
     *            notifications received.
     * @throws UnknownHostException
     */
    public GroupServer(final String groupAddress, final int port, final Collection<String> notifications)
            throws UnknownHostException {
        this.groupAddress = InetAddress.getByName(groupAddress);
        this.port = port;
        this.notifications = notifications;
    }


    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        final MulticastSocket sock = tryGetMulticastSocket();

        log.debug("joining group {}", groupAddress.getHostAddress());
        tryJoinGroup(sock);
        log.debug("entering receive loop");

        while (!Thread.currentThread().isInterrupted()) {
            final byte buf[] = new byte[256];
            final DatagramPacket pack = new DatagramPacket(buf, buf.length);

            try {
                sock.receive(pack);
            } catch (final IOException e) {
                tryLeaveGroup(sock);
                sock.close();

                throw new RuntimeException(e);
            }

            final String message = new String(pack.getData(), 0, pack.getLength());

            log.trace("<< '{}'", message);
            notifications.add(message);
        }

        log.debug("leaving group {}", groupAddress.getHostAddress());
        tryLeaveGroup(sock);
        sock.close();
    }


    /**
     * @return a {@link MulticastSocket} bound to <code>port</code>.
     */
    private MulticastSocket tryGetMulticastSocket() {
        try {
            return new MulticastSocket(new InetSocketAddress(port));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param sock
     */
    private void tryJoinGroup(final MulticastSocket sock) {
        try {
            sock.joinGroup(groupAddress);
        } catch (final IOException e) {
            sock.close();

            throw new RuntimeException(e);
        }
    }


    /**
     * @param sock
     */
    private void tryLeaveGroup(final MulticastSocket sock) {
        try {
            sock.joinGroup(groupAddress);
        } catch (final IOException e) {
            sock.close();

            throw new RuntimeException(e);
        }
    }
}
