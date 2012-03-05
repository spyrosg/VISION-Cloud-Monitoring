package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;


/**
 *
 */
public class InstanceManager {
    /**
     *
     */
    private static class PIDServer extends Thread {
        /** the port to bind to */
        private final int port;


        /**
         * Constructor.
         * 
         * @param port
         *            the port to bind to
         */
        PIDServer(final int port) {
            super( "udp-pid-server" );
            this.port = port;
        }


        /**
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            final DatagramSocket sock;

            try {
                sock = new DatagramSocket( port );
                sock.setReuseAddress( true );
            } catch( final SocketException e ) {
                throw new RuntimeException( e );
            }

            while( !isInterrupted() )
                try {
                    final byte[] reqBuffer = new byte[64];
                    final DatagramPacket req = new DatagramPacket( reqBuffer, reqBuffer.length );

                    sock.receive( req );

                    final String dgram = new String( req.getData(), 0, req.getLength() );

                    if( !dgram.equals( PID_CMD ) )
                        continue;

                    final byte[] resBuffer = String.valueOf( getPID() ).getBytes();
                    final DatagramPacket res = new DatagramPacket( resBuffer, resBuffer.length, req.getAddress(), req.getPort() );

                    sock.send( res );
                } catch( final IOException e ) {
                    if( !isInterrupted() )
                        throw new RuntimeException( e );
                }

            sock.close();
        }
    }

    /***/
    private static final int    PID_SERVER_PORT  = 56431;
    /***/
    private static final String PID_CMD          = "pid?";
    /***/
    private static final String KILL_CMD         = "kill!";
    /***/
    private static final int    RESPONSE_TIMEOUT = 1000;
    /***/
    private final PIDServer     server;


    /**
     * Constructor.
     */
    public InstanceManager() {
        this.server = new PIDServer( PID_SERVER_PORT );
    }


    /**
     * Start the manager.
     */
    public void start() {
        server.start();
    }


    /**
     * @return
     * @throws IOException
     */
    public int status() throws IOException {
        return getRunningInstancePID();
    }


    /**
     * Stop the manager.
     */
    public void stop() {
        server.interrupt();
    }


    /**
     * @return this vm's pid.
     */
    static int getPID() {
        // NOTE: expecting something like '<pid>@<hostname>'
        final String vmname = ManagementFactory.getRuntimeMXBean().getName();
        final int atIndex = vmname.indexOf( "@" );

        if( atIndex < 0 )
            throw new Error( "Cannot get pid: pid N/A for this jvm." );

        return Integer.valueOf( vmname.substring( 0, atIndex ) );
    }


    /**
     * @return
     * @throws IOException
     */
    private static int getRunningInstancePID() throws IOException {
        final DatagramSocket sock = new DatagramSocket();

        try {
            final byte[] reqBuffer = PID_CMD.getBytes();
            final DatagramPacket req = new DatagramPacket( reqBuffer, reqBuffer.length, InetAddress.getLocalHost(),
                    PID_SERVER_PORT );

            sock.setSoTimeout( RESPONSE_TIMEOUT );
            sock.send( req );

            final byte[] resBuffer = new byte[64];
            final DatagramPacket res = new DatagramPacket( resBuffer, resBuffer.length );

            sock.receive( res );

            return Integer.parseInt( new String( res.getData(), 0, res.getLength() ) );
        } catch( final SocketTimeoutException e ) {
            throw e;
        } finally {
            sock.close();
        }
    }
}
