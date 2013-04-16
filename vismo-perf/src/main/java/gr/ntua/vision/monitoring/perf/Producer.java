package gr.ntua.vision.monitoring.perf;

import gr.ntua.vision.monitoring.dispatch.VismoEventDispatcher;
import gr.ntua.vision.monitoring.web.WebAppBuilder;
import gr.ntua.vision.monitoring.web.WebServer;

import java.io.File;


/**
 * 
 */
public class Producer {
    /***/
    private static final int    PORT = 9991;
    /***/
    private static final String PROG = "Producer";
    /***/
    private final String        configFile;
    /***/
    private final WebServer     server;


    /**
     * Constructor.
     * 
     * @param configFile
     * @param port
     */
    private Producer(final String configFile, final int port) {
        this.configFile = configFile;
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
        final VismoEventDispatcher dispatcher = new VismoEventDispatcher(configFile, "producer");
        final FakeObjectService service = new FakeObjectService(dispatcher);

        for (int i = 0; i < noEvents; ++i)
            service.send();
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
            System.err.println(PROG + " config [port:-" + PORT + "]");
            System.exit(1);
        }

        final String configFile = args[0];
        final int port = args.length == 2 ? Integer.valueOf(args[1]) : PORT;

        requireFile(configFile);

        new Producer(configFile, port).start();
    }


    /**
     * @param path
     * @throws Error
     */
    private static void requireFile(final String path) throws Error {
        if (!new File(path).exists())
            throw new Error("no such file or directory: " + path);
    }
}
