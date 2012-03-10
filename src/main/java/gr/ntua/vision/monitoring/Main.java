package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;


/**
 * The entry point to the monitoring instance.
 */
public class Main {
    /** the configuration object. */
    private static final Config cnf  = new Config();
    /** the program name. */
    private static final String PROG = "vismo";


    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {
        if( args.length == 0 ) {
            showHelp();
            return;
        }

        final String cmd = args[0];

        if( cmd.equals( "start" ) )
            start();
        else if( cmd.equals( "status" ) || cmd.equals( "stop" ) )
            stopOrStatus( cmd );
        else
            showHelp();
    }


    /**
     * 
     */
    private static void showHelp() {
        // TODO Auto-generated method stub
    }


    /**
     * @throws SocketException
     */
    private static void start() throws SocketException {
        new InstanceManager( cnf, null ).start(); // FIXME
    }


    /**
     * @param cmd
     * @throws IOException
     */
    private static void stopOrStatus(final String cmd) throws IOException {
        final CommandClient client = new CommandClient( cnf );

        if( cmd.equals( "status" ) )
            try {
                System.out.println( PROG + ": running, pid: " + client.status() );
            } catch( final SocketTimeoutException e ) {
                System.out.println( PROG + ": stopped" );
            }
        else
            try {
                client.stop();
                System.out.println( PROG + ": stopping" );
            } catch( final SocketTimeoutException e ) {
                System.out.println( PROG + ": is already stopped" );
            }
    }
}
