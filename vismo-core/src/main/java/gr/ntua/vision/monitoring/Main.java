package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.udp.UDPClient;
import gr.ntua.vision.monitoring.udp.UDPFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;


/**
 * The entry point to the monitoring instance.
 */
public class Main {
    /***/
    private static final long   ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);
    /** the program name. */
    private static final String PROG       = "vismo";


    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {
        if (args.length != 2) {
            showHelp();
            System.exit(1);
        }

        final VismoConfiguration config = new VismoConfiguration(args[0]);
        final String command = args[1];

        if (command.equals("start")) {
            final VismoService service = new VismoFactory(config).build();

            service.start();

            /*final OldVismoNode vismo = new VismoNodeFactory(config).build();

            vismo.start();
            final ClusterController controller = new ClusterController(new VismoVMInfo(), config);
            final VismoCloudElement elem = controller.setup();
            final UDPServer server = new UDPFactory(config.getUDPPort()).buildServer((UDPListener) elem);

            // final VismoService service = new VismoService(elem);

            // service.addTimerTask(new JVMStatusReportTask(ONE_MINUTE));
            // service.addTask(new UDPFactory(config.getUDPPort()).buildServer(service));
            // service.start();

            server.start();
            ((AbstractVismoCloudElement) elem).addTask(server);
            // ((AbstractVismoCloudElement) elem).addTimerTask(new JVMStatusReportTask(ONE_MINUTE));*/
        } else {
            final UDPClient client = new UDPFactory(config.getUDPPort()).buildClient();

            if (command.equals("status"))
                reportVismoStatus(client);
            else
                shutdownVismo(client);
        }
    }


    /**
     * @param client
     * @throws IOException
     */
    private static void reportVismoStatus(final UDPClient client) throws IOException {
        String resp = null;

        for (int i = 0; i < 3; ++i)
            try {
                resp = client.getVismoStatus();
                break;
            } catch (final SocketTimeoutException e) {
                //
            }

        if (resp == null)
            System.err.println(PROG + ": service is stopped.");
        else
            System.err.println(PROG + ": running, pid: " + Integer.parseInt(resp));
    }


    /**
     * 
     */
    private static void showHelp() {
        System.err.println("Usage: java -jar " + PROG + ".jar config-file command");
        System.err.println("Commands:\n");
        System.err.println("  start   start a vismo instance.");
        System.err.println("  status  report the status of vismo.");
        System.err.println("  stop    stop any running vismo instance.");
    }


    /**
     * @param client
     * @throws IOException
     */
    private static void shutdownVismo(final UDPClient client) throws IOException {
        try {
            System.err.println(PROG + ": stopping.");
            client.shutdownVismo();
        } catch (final SocketTimeoutException e) {
            System.err.println(PROG + " is stopped.");
        }
    }
}
