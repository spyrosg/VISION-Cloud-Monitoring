package gr.ntua.vision.monitoring.mon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This supports one way communication to a multi-cast group.
 */
public class GroupClient {
    /** the log target. */
    private static final Logger log = LoggerFactory.getLogger(GroupClient.class);
    /** the group address. */
    private final InetAddress   groupAddress;
    /** the group port. */
    private final int           port;


    /**
     * Constructor.
     * 
     * @param groupAddress
     *            the group address.
     * @param port
     *            the group port.
     * @throws UnknownHostException
     */
    public GroupClient(final String groupAddress, final int port) throws UnknownHostException {
        this.groupAddress = InetAddress.getByName(groupAddress);
        this.port = port;
    }


    /**
     * Send the given notification, without waiting for reply, to the group.
     * 
     * @param note
     *            the note.
     * @throws IOException
     */
    public void notifyGroup(final String note) throws IOException {
        final MulticastSocket sock = new MulticastSocket();
        final byte[] buf = note.getBytes();
        final DatagramPacket p = new DatagramPacket(buf, buf.length, groupAddress, port);

        sock.setNetworkInterface(NetworkInterface.getByName("eth0"));
        sock.setTimeToLive(5); // be somewhat liberal on the socket
        log.trace(">> '{}' group {}:{}", new Object[] { note, groupAddress.getHostAddress(), port });

        try {
            sock.send(p);
        } finally {
            sock.close();
        }
    }
}
