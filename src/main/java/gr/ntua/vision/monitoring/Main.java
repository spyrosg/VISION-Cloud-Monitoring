package gr.ntua.vision.monitoring;

import java.io.IOException;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


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
            String getHelpString() {
                return "show this help message and exit.";
            }


            @Override
            void run(final Config ignored) throws IOException {
                System.err.println("Usage: java -jar " + PROG + ".jar");
                System.err.println("Options:\n");

                for (final Commands cmd : Commands.values())
                    System.err.println(String.format("  %-10s%-40s", cmd.name().toLowerCase(), cmd.getHelpString()));
            }
        },
        /***/
        START("start") {
            public void send(final Socket sock, final String message) {
                sock.send(message.getBytes(), 0);
            }


            @Override
            String getHelpString() {
                return "start the service.";
            }


            @Override
            void run(final Config cnf) throws IOException {
                final ZContext ctx = new ZContext();
                final Socket s = ctx.createSocket(ZMQ.REQ);

                s.connect("ipc://foo");
                send(s, "connected");
            }
        },
        /***/
        STATUS("status") {
            @Override
            String getHelpString() {
                return "print the service status.";
            }


            @Override
            void run(final Config cnf) throws IOException {
            }
        },
        /***/
        STOP("stop") {
            @Override
            String getHelpString() {
                return "stop the service.";
            }


            @Override
            void run(final Config cnf) throws IOException {
            }
        };

        /** the command name. */
        private final String name;


        /**
         * Constructor.
         * 
         * @param name
         *            the command name.
         */
        private Commands(final String name) {
            this.name = name;
        }


        /**
         * @return the corresponding explanatory command message.
         */
        abstract String getHelpString();


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
            for (final Commands cmd : Commands.values())
                if (cmd.name.equals(str))
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
        if (args.length == 0 || !Commands.isValidCommand(args[0])) {
            Commands.HELP.run(null);
            return;
        }

        final Config cnf = new Config();
        final Commands cmd = Commands.valueOf(args[0].toUpperCase());

        cmd.run(cnf);
    }
}
