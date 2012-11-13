package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.udp.UDPClient;
import gr.ntua.vision.monitoring.udp.UDPFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;


/**
 * The entry point to the monitoring instance.
 */
public class Main {
    /** the program name. */
    private static final String PROG = "vismo";


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
        final VMInfo vminfo = new VismoVMInfo();

        if (command.equals("start")) {
            final VismoService service = new VismoFactory(config).build(vminfo);

            service.start();
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
