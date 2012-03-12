package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.lifecycle.Supervisor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


/**
 * 
 */
public class CommandServer implements Runnable {
    /** the configuration object. */
    private final Config         cnf;
    /** the socket. */
    private final DatagramSocket sock;
    /***/
    private final Supervisor     supervisor;


    /**
     * Constructor.
     * 
     * @param cnf
     *            the configuration object.
     * @param supervisor
     * @throws SocketException
     */
    public CommandServer(final Config cnf, final Supervisor supervisor) throws SocketException {
        this.cnf = cnf;
        this.supervisor = supervisor;
        this.sock = new DatagramSocket( cnf.getPort() );
        this.sock.setReuseAddress( true );
    }


    /**
     * This is used to interrupt/stop the execution of the thread running the server. FIXME: add expected usage, call order.
     */
    public void closeConnection() {
        sock.close();
    }


    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while( !Thread.currentThread().isInterrupted() )
            try {
                final DatagramPacket req = receive();
                final String cmd = new String( req.getData(), 0, req.getLength() );

                if( cmd.equals( cnf.getStatusCommand() ) ) {
                    send( String.valueOf( cnf.getPID() ), req.getAddress(), req.getPort() );
                    continue;
                }
                if( cmd.equals( cnf.getKillCommand() ) ) {
                    send( "ok", req.getAddress(), req.getPort() );
                    supervisor.stop();
                    break;
                }
            } catch( final IOException e ) {
                if( !Thread.currentThread().isInterrupted() )
                    throw new RuntimeException( e );
            } finally {
                sock.close();
            }
    }


    /**
     * Receive a datagram packet from the socket.
     * 
     * @return a {@link DatagramPacket}.
     * @throws IOException
     */
    private DatagramPacket receive() throws IOException {
        final byte[] buf = new byte[64];
        final DatagramPacket req = new DatagramPacket( buf, buf.length );

        sock.receive( req );

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
        final DatagramPacket res = new DatagramPacket( buf, buf.length, addr, port );

        sock.send( res );
    }
}
