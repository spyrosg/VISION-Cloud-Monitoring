package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 * This is used to query any running monitoring instance.
 */
public class CommandClient {
    /** the configuration object. */
    private final Config cnf;


    /**
     * Constructor.
     * 
     * @param cnf
     *            the configuration object.
     */
    public CommandClient(final Config cnf) {
        this.cnf = cnf;
    }


    /**
     * @return the PID of the monitoring instance.
     * @throws IOException
     */
    public int status() throws IOException {
        final DatagramSocket sock = new DatagramSocket();

        try {
            sock.setSoTimeout( cnf.getTimeout() );
            send( sock, cnf.getStatusCommand(), InetAddress.getLocalHost(), cnf.getPort() );

            final DatagramPacket dgram = receive( sock );

            return Integer.parseInt( new String( dgram.getData(), 0, dgram.getLength() ) );
        } finally {
            sock.close();
        }
    }


    /**
     * Ask the running monitoring instance to stop.
     * 
     * @throws IOException
     */
    public void stop() throws IOException {
        final DatagramSocket sock = new DatagramSocket();

        try {
            sock.setSoTimeout( cnf.getTimeout() );
            send( sock, cnf.getKillCommand(), InetAddress.getLocalHost(), cnf.getPort() );
            receive( sock );
        } finally {
            sock.close();
        }
    }


    /**
     * Receive a datagram packet from the given socket.
     * 
     * @param sock
     *            the socket.
     * @return the datagram received.
     * @throws IOException
     */
    private static DatagramPacket receive(final DatagramSocket sock) throws IOException {
        final byte[] buf = new byte[64];
        final DatagramPacket req = new DatagramPacket( buf, buf.length );

        sock.receive( req );

        return req;
    }


    /**
     * Send as a datagram packet the given content, through the socket.
     * 
     * @param sock
     *            the socket.
     * @param payload
     *            the payload to send.
     * @param addr
     *            the address to sent to.
     * @param port
     *            the port to sent to.
     * @throws IOException
     */
    private static void send(final DatagramSocket sock, final String payload, final InetAddress addr, final int port)
            throws IOException {
        final byte[] buf = payload.getBytes();
        final DatagramPacket req = new DatagramPacket( buf, buf.length, addr, port );

        sock.send( req );
    }
}
