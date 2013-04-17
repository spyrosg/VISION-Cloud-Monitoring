package gr.ntua.vision.monitoring.perf;

import static gr.ntua.vision.monitoring.perf.Utils.requireFile;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.web.WebAppBuilder;
import gr.ntua.vision.monitoring.web.WebServer;

import java.util.ArrayList;


/**
 * 
 */
public class Consumer {
    /**
     * 
     */
    private static class PerfHandler implements EventHandler {
        /***/
        private int          noReceivedEvents;
        /***/
        private final String topic;


        /**
         * Constructor.
         */
        public PerfHandler() {
            this("*");
        }


        /**
         * Constructor.
         * 
         * @param topic
         */
        public PerfHandler(final String topic) {
            this.topic = topic;
            this.noReceivedEvents = 0;
        }


        /**
         * @return the noReceivedEvents
         */
        public int getNoReceivedEvents() {
            return noReceivedEvents;
        }


        /**
         * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
         */
        @Override
        public void handle(final MonitoringEvent e) {
            if (e == null)
                return;

            ++noReceivedEvents;

            final long now = System.currentTimeMillis();

            System.out.println("now=" + now + ", ts=" + e.timestamp() + ", latency=" + getLatency(now, e));
        }


        /**
         * 
         */
        public void reset() {
            noReceivedEvents = 0;
        }


        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "#<PerfHandler: " + topic + ">";
        }


        /**
         * @param now
         * @param e
         * @return d
         */
        private static double getLatency(final long now, final MonitoringEvent e) {
            return (now - e.timestamp()) / 1000.0;
        }
    }

    /***/
    private static final int             PORT     = 9992;
    /***/
    private static final String          PROG     = "Consumer";
    /***/
    private final ArrayList<PerfHandler> handlers = new ArrayList<PerfHandler>();
    /***/
    private final VismoEventRegistry     registry;
    /***/
    private final WebServer              server;


    /**
     * Constructor.
     * 
     * @param configFile
     * @param port
     */
    private Consumer(final String configFile, final int port) {
        this.server = new WebServer(port);
        this.registry = new VismoEventRegistry(configFile);
    }


    /**
     * @return s
     */
    String getHandlers() {
        final StringBuilder buf = new StringBuilder();

        for (int i = 0; i < handlers.size(); ++i)
            buf.append(i).append(": ").append(handlers.get(i)).append("\n");

        return buf.toString();
    }


    /**
     * @param i
     * @return n
     */
    int getNoReceivingEvents(final int i) {
        final PerfHandler h = handlers.get(i);

        System.out.println("handler " + h + " has received " + h.getNoReceivedEvents() + " events");

        return h.getNoReceivedEvents();
    }


    /**
     * @throws Exception
     */
    void halt() throws Exception {
        server.stop();
    }


    /**
     * @return n
     */
    int registerHandler() {
        final PerfHandler handler = new PerfHandler();

        registry.registerToAll(handler);
        handlers.add(handler);

        return handlers.size() - 1;
    }


    /**
     * @param topic
     * @return n
     */
    int registerHandler(final String topic) {
        final PerfHandler handler = new PerfHandler(topic);

        registry.register(topic, handler);
        handlers.add(handler);

        return handlers.size() - 1;
    }


    /**
     * @param i
     */
    void resetHandler(final int i) {
        final PerfHandler h = handlers.get(i);

        System.out.println("handler " + h + " reset");

        h.reset();
    }


    /**
     * @throws Exception
     */
    private void start() throws Exception {
        server.withWebAppAt(WebAppBuilder.buildFrom(new ConsumersCommandResource(this)), "/*").start();
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

        new Consumer(configFile, port).start();
    }
}
