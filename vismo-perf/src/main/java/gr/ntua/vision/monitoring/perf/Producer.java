package gr.ntua.vision.monitoring.perf;

import static gr.ntua.vision.monitoring.perf.Utils.requireFile;
import gr.ntua.vision.monitoring.dispatch.VismoEventDispatcher;
import gr.ntua.vision.monitoring.web.WebAppBuilder;
import gr.ntua.vision.monitoring.web.WebServer;


/**
 * 
 */
public class Producer {
    /***/
    private static final int        PORT = 9991;
    /***/
    private static final String     PROG = "Producer";
    /***/
    private final WebServer         server;
    /***/
    private final FakeObjectService service;


    /**
     * Constructor.
     * 
     * @param configFile
     * @param port
     */
    private Producer(final String configFile, final int port) {
        this.service = new FakeObjectService(new VismoEventDispatcher(configFile, "producer"));
        this.server = new WebServer(port);
    }


    /**
     * @throws Exception
     */
    void halt() throws Exception {
        server.stop();
    }


    /**
     * @param noEvents
     */
    void sendEvents(final int noEvents) {
        for (int i = 0; i < noEvents; ++i)
            service.send();
    }


    /**
     * @param topic
     * @param noEvents
     */
    void sendEvents(final String topic, final int noEvents) {
        for (int i = 0; i < noEvents; ++i)
            service.send(topic);
    }


    /**
     * @throws Exception
     */
    void start() throws Exception {
        server.withWebAppAt(WebAppBuilder.buildFrom(new ProducersCommandResource(this)), "/*").start();
    }


    /**
     * @param args
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        if (args.length < 1) {
            System.err.println("arg count");
            System.err.println(PROG + " config-file [port:-" + PORT + "]");
            System.exit(1);
        }

        final String configFile = args[0];
        final int port = args.length == 2 ? Integer.valueOf(args[1]) : PORT;

        requireFile(configFile);

        new Producer(configFile, port).start();
    }
}
