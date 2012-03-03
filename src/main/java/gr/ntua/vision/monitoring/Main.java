package gr.ntua.vision.monitoring;

import java.net.SocketException;
import java.net.SocketTimeoutException;


/**
 *
 */
public class Main {
    /***/
    private static final String PROG = "vismo";


    /**
     * @param args
     * @throws SocketException
     */
    public static void main(final String... args) throws SocketException {
        if( args.length == 0 ) {
            showHelp();
            System.exit( 1 );
        }

        final InstanceManager man = new InstanceManager();

        if( args[0].equals( "start" ) )
            man.start();
        else if( args[0].equals( "stop" ) )
            man.stop();
        else if( args[0].equals( "status" ) )
            try {
                final int pid = man.status();

                System.out.println( PROG + ": running with pid " + pid );
            } catch( final SocketTimeoutException e ) {
                System.out.println( PROG + ": stopped" );
            }
        else {
            showHelp();
            System.exit( 1 );
        }
    }


    /**
     * 
     */
    private static void showHelp() {
        // TODO Auto-generated method stub
    }
}
