package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.net.SocketTimeoutException;


/**
 * The entry point to the monitoring instance.
 */
public class Main {
    /**
     * List of available commands understood by the server. They match standard UNIX init.d commands.
     */
    private enum Commands {
        /***/
        HELP("help") {
            @Override
            void run(final Config cnf) throws IOException {
                System.err.println( "help message" ); // FIXME
            }
        },
        /***/
        START("start") {
            @Override
            void run(final Config cnf) throws IOException {
                // new InstanceManager( cnf, null ).start(); // FIXME
                System.out.println( "running" );
                while( true )
                    try {
                        Thread.sleep( 10000 );
                    } catch( final InterruptedException e ) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            }
        },
        /***/
        STATUS("status") {
            @Override
            void run(final Config cnf) throws IOException {
                final CommandClient client = new CommandClient( cnf );

                try {
                    System.out.println( PROG + ": running, pid: " + client.status() );
                } catch( final SocketTimeoutException e ) {
                    System.out.println( PROG + ": stopped" );
                }
            }
        },
        /***/
        STOP("stop") {
            @Override
            void run(final Config cnf) throws IOException {
                final CommandClient client = new CommandClient( cnf );

                try {
                    client.stop();
                    System.out.println( PROG + ": stopping" );
                } catch( final SocketTimeoutException e ) {
                    System.out.println( PROG + ": is already stopped" );
                }
            }
        };

        /** the command name. */
        public final String c;


        /**
         * Constructor.
         * 
         * @param c
         *            the command name.
         */
        private Commands(final String c) {
            this.c = c;
        }


        /**
         * Execute the command.
         * 
         * @param cnf
         *            the configuration object.
         * @throws IOException
         */
        abstract void run(final Config cnf) throws IOException;


        /**
         * Check that given string is indeed a valid server command.
         * 
         * @param str
         *            the user specified command.
         * @return <code>true</code> iff the string is a valid server command, <code>false</code> otherwise.
         */
        public static boolean isValidCommand(final String str) {
            for( final Commands cmd : Commands.values() )
                if( cmd.c.equals( str ) )
                    return true;

            return false;
        }
    }

    /** the program name. */
    private static final String PROG = "vismo";


    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {
        final Config cnf = new Config();

        if( args.length == 0 || !Commands.isValidCommand( args[0] ) ) {
            Commands.HELP.run( cnf );
            return;
        }

        final Commands cmd = Commands.valueOf( args[0].toUpperCase() );

        cmd.run( cnf );
    }
}
