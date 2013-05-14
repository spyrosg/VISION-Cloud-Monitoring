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
    private static final int    PORT = 9991;
    /***/
    private static final String PROG = "Producer";
    /***/
    private final WebServer     server;
    /***/
    private final EventService  service;


    /**
     * Constructor.
     * 
     * @param service
     * @param port
     */
    private Producer(final EventService service, final int port) {
        this.service = service;
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
     * @param size
     */
    void sendEvents(final int noEvents, final long size) {
        final long start = System.currentTimeMillis();
        service.send(noEvents, size);
        final double dur = (System.currentTimeMillis() - start) / 1000.0;

        System.out.println("sent " + noEvents + " events of size " + size + " bytes in " + dur + " seconds (" + noEvents / dur
                + " ev/sec)");
    }


    /**
     * @param topic
     * @param noEvents
     * @param size
     */
    void sendEvents(final String topic, final int noEvents, final long size) {
        final long start = System.currentTimeMillis();
        service.send(topic, noEvents, size);
        final double dur = (System.currentTimeMillis() - start) / 1000.0;

        System.out.println("sent " + noEvents + " events of size " + size + " bytes in " + dur + " seconds (" + noEvents / dur
                + " ev/sec)");
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

        final VismoEventDispatcher dispatcher = new VismoEventDispatcher(configFile, "constant-size-service");
        final ConstantSizeEventService service = new ConstantSizeEventService(dispatcher);

        new Producer(service, port).start();
    }
}
