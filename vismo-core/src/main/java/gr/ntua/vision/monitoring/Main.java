package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.policy.StaticConfigPolicy;
import gr.ntua.vision.monitoring.service.VismoService;
import gr.ntua.vision.monitoring.udp.UDPClient;
import gr.ntua.vision.monitoring.udp.UDPFactory;
import gr.ntua.vision.monitoring.udp.UDPServer;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The entry point to the monitoring instance.
 */
public class Main {
    /***/
    private static final Logger      log  = LoggerFactory.getLogger(Main.class);
    /** the program name. */
    private static final String      PROG = "vismo";
    /***/
    private final VismoConfiguration conf;


    /**
     * Constructor.
     * 
     * @param conf
     */
    private Main(final VismoConfiguration conf) {
        this.conf = conf;
    }


    /**
     * @throws IOException
     */
    public void start() throws IOException {
        logVersion();

        final VismoService service = (VismoService) new StaticConfigPolicy(conf).build(new VismoVMInfo());
        final UDPServer udpServer = new UDPFactory(conf.getUDPPort()).buildServer();

        udpServer.add(service);
        udpServer.start();
        service.start();
    }


    /**
     * @throws IOException
     */
    public void status() throws IOException {
        final UDPClient client = new UDPFactory(conf.getUDPPort()).buildClient();

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
     * @throws IOException
     */
    public void stop() throws IOException {
        final UDPClient client = new UDPFactory(conf.getUDPPort()).buildClient();

        try {
            client.shutdownVismo();
            System.err.println(PROG + ": stopping.");
        } catch (final SocketTimeoutException e) {
            System.err.println(PROG + " is stopped.");
        }
    }


    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {
        if (args.length != 2) {
            showHelp();
            System.exit(1);
        }

        final Main main = new Main(new VismoConfiguration(args[0]));
        final String command = args[1];

        if ("start".equals(command))
            main.start();
        else if ("status".equals(command))
            main.status();
        else if ("stop".equals(command))
            main.stop();
        else
            showHelp();
    }


    /**
     * Log version, commit id and build date.
     * 
     * @throws IOException
     */
    private static void logVersion() throws IOException {
        final Properties p = new Properties();

        p.load(Main.class.getResourceAsStream("/git.properties"));
        log.info("this is {}, running off commit-id {}, built on {}", new String[] { PROG, p.getProperty("git.commit.id.abbrev"),
                p.getProperty("git.build.time") });
    }


    /**
     * Print the help message.
     */
    private static void showHelp() {
        System.err.println("Usage: java -jar " + PROG + ".jar config-file command");
        System.err.println("Commands:\n");
        System.err.println("  start   start a vismo instance.");
        System.err.println("  status  report the status of vismo.");
        System.err.println("  stop    stop any running vismo instance.");
        System.err.println("  help    show this help message and exit.");
    }
}
