package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.web.WebAppBuilder;
import gr.ntua.vision.monitoring.web.WebServer;


/**
 *
 */
public class CDMIQueueService {
    /***/
    private static final String PROG = "vismo-queues";
    /***/
    private final String[]      args;


    /**
     * Constructor.
     * 
     * @param args
     */
    private CDMIQueueService(final String[] args) {
        this.args = args;
    }


    /**
     * @param opt
     * @return the argument that corresponds to the option.
     */
    private String getOptArg(final String opt) {
        for (int i = 0; i < args.length; ++i)
            if (opt.equals(args[i])) {
                if (i + 1 < args.length)
                    return args[i + 1];

                return null;
            }

        return null;
    }


    /**
     * @throws Exception
     */
    private void run() throws Exception {
        if (args.length == 0 || args[0].startsWith("-h")) {
            showHelp();
            System.exit(0);
        }

        final int port = Integer.valueOf(getOptArg("-p"));
        final int queueSize = Integer.valueOf(getOptArg("-s"));
        final WebServer server = new WebServer(port);
        final WebAppBuilder builder = new WebAppBuilder();

        // connect to localhost, hopefully on the cloud head
        final VismoEventRegistry registry = new VismoEventRegistry("tcp://localhost:56430"); 
        final QueuesRegistry queuesRegistry = new QueuesRegistry(registry, queueSize);

        builder.addProvider(CDMIQueueProdiver.class);
        builder.addResource(new CDMIQueuesResource(queuesRegistry));

        server.withWebAppAt(builder.build(), "/api/*").withStaticResourcesAt("/static", "/*");
        server.start();
    }


    /**
     * @param args
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        new CDMIQueueService(args).run();
    }


    /**
     * 
     */
    private static void showHelp() {
        System.err.println(PROG + ": usage: java -jar " + PROG + ".jar -p port -s queue-size");
    }
}
