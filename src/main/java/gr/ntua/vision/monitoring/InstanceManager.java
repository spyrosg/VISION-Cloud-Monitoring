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

            while( !Thread.currentThread().isInterrupted() )
                try {
                    final byte[] reqBuffer = new byte[64];
                    final DatagramPacket req = new DatagramPacket( reqBuffer, reqBuffer.length );

                    System.err.println( "server: receiving" );
                    sock.receive( req );

                    final String dgram = new String( reqBuffer, 0, reqBuffer.length );

                    System.err.println( "server: received: " + dgram );

                    if( !dgram.equals( REQ_WORD ) )
                        continue;

                    final byte[] resBuffer = String.valueOf( getPID() ).getBytes();
                    final DatagramPacket res = new DatagramPacket( resBuffer, resBuffer.length, req.getAddress(), req.getPort() );

                    System.err.println( "server: sending: " + getPID() );
                    sock.send( res );
                } catch( final IOException e ) {
                    if( !isInterrupted() )
                        e.printStackTrace();
                }

            sock.close();
        }
    }

    /***/
    private static final int    PID_SERVER_PORT  = 56431;
    /***/
    private static final String REQ_WORD         = "pid?";
    /***/
    private static final int    RESPONSE_TIMEOUT = 1000;
    /***/
    private final PIDServer     server;


    /**
     * Constructor.
     * 
     * @throws SocketException
     */
    public InstanceManager() throws SocketException {
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
     * @throws SocketTimeoutException
     * @throws SocketException
     */
    public int status() throws SocketTimeoutException, SocketException {
        return getRunningInstancePID();
    }


    /**
     * Stop the manager.
     * 
     * @return
     */
    public boolean stop() {
        server.interrupt();
        return server.isAlive();
    }


    /**
     * @return this vm's pid.
     */
    public static int getPID() {
        // NOTE: expecting something like '<pid>@<hostname>'
        final String vmname = ManagementFactory.getRuntimeMXBean().getName();
        final int atIndex = vmname.indexOf( "@" );

        if( atIndex < 0 )
            throw new Error( "Cannot get pid: pid N/A for this jvm." );

        return Integer.valueOf( vmname.substring( 0, atIndex ) );
    }


    /**
     * @return
     * @throws SocketTimeoutException
     * @throws SocketException
     */
    private static int getRunningInstancePID() throws SocketTimeoutException, SocketException {
        for( int i = 0; i < 3; ++i ) {
            final DatagramSocket sock = new DatagramSocket();

            try {
                sock.setSoTimeout( RESPONSE_TIMEOUT );
                final byte[] reqBuffer = REQ_WORD.getBytes();
                final DatagramPacket req = new DatagramPacket( reqBuffer, reqBuffer.length, InetAddress.getLocalHost(),
                        PID_SERVER_PORT );

                sock.send( req );

                final byte[] resBuffer = new byte[64];
                final DatagramPacket res = new DatagramPacket( resBuffer, reqBuffer.length );

                sock.receive( res );

                final String response = new String( resBuffer, 0, resBuffer.length );

                return Integer.valueOf( response );
            } catch( final SocketTimeoutException e ) {
                throw e;
            } catch( final IOException e ) {
                throw new RuntimeException( e );
            } finally {
                sock.close();
            }
        }

        throw new SocketTimeoutException();
    }
}
