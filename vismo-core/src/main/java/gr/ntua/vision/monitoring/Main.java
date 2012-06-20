package gr.ntua.vision.monitoring;

import gr.ntua.vision.Configuration;
import gr.ntua.vision.PropertiesConfiguration;
import gr.ntua.vision.monitoring.udp.UDPClient;
import gr.ntua.vision.monitoring.udp.UDPServer;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.zeromq.ZContext;


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
                return "print this help message and exit.";
            }


            @Override
            void run(final Configuration ignored) throws IOException {
                System.err.println("Usage: java -jar " + PROG + ".jar");
                System.err.println("Options:\n");

                for (final Commands cmd : Commands.values())
                    System.err.println(String.format("  %-10s%-40s", cmd.name().toLowerCase(), cmd.getHelpString()));
            }
        },
        /***/
        START("start") {
            @Override
            String getHelpString() {
                return "start the service.";
            }


            @Override
            void run(final Configuration cnf) throws IOException {
                final MonitoringInstance mon = new MonitoringInstance(new VismoVMInfo());
                final ZContext ctx = new ZContext();
                final LocalEventCollector receiver = new LocalEventCollector(ctx, EVENTS_END_POINT);

                receiver.subscribe(new EventDistributor(ctx, DISTRIBUTION_POINT));

                mon.addTask(receiver);
                System.err.println("keys: " + cnf.keys());
                System.err.println("port: " + cnf.get("udp.port"));
                mon.addTask(new UDPServer(Integer.valueOf(cnf.get("udp.port")), mon));
                mon.start();
            }
        },
        /***/
        STATUS("status") {
            @Override
            String getHelpString() {
                return "print the service status.";
            }


            @Override
            void run(final Configuration cnf) throws IOException {
                final UDPClient client = new UDPClient(Integer.valueOf(cnf.get("udp.port")));
                String resp = null;

                for (int i = 0; i < 3; ++i)
                    try {
                        resp = client.getServiceStatus();
                        break;
                    } catch (final SocketTimeoutException e) {
                        //
                    }

                if (resp == null)
                    System.out.println(PROG + ": service is stopped.");
                else
                    System.out.println(PROG + ": running, pid: " + Integer.parseInt(resp));
            }
        },
        /***/
        STOP("stop") {
            @Override
            String getHelpString() {
                return "stop the service.";
            }


            @Override
            void run(final Configuration cnf) throws IOException {
                final UDPClient client = new UDPClient(Integer.valueOf(cnf.get("udp.port")));

                try {
                    System.out.println(PROG + ": stopping.");
                    client.shutdownService();
                } catch (final SocketTimeoutException e) {
                    System.out.println(PROG + " is stopped.");
                }
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
        abstract void run(final Configuration cnf) throws IOException;


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

    /** listen on all available interfaces when distributing events to consumers. */
    private static final String DISTRIBUTION_POINT = "tcp://*:27890";
    /** this is the endpoint used to send/receive events. */
    private static final String EVENTS_END_POINT   = "tcp://127.0.0.1:26891";
    /** the program name. */
    private static final String PROG               = "vismo";
    /***/
    private static final String VISMO_CONFIG       = "/vismo.properties";


    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {
        if (args.length == 0 || !Commands.isValidCommand(args[0])) {
            Commands.HELP.run(null);
            return;
        }

        final Configuration cnf = PropertiesConfiguration.loadFromFile("../src/main/resources/vismo.properties");
        final Commands cmd = Commands.valueOf(args[0].toUpperCase());

        cmd.run(cnf);
    }
}
