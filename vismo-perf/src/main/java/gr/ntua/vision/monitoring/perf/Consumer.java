package gr.ntua.vision.monitoring.perf;

import static gr.ntua.vision.monitoring.perf.Utils.requireFile;
import gr.ntua.vision.monitoring.VismoConfiguration;
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
        private final int    eventsPerByte;
        /***/
        private int          noReceivedEvents;
        /***/
        private final String topic;


        /**
         * Constructor.
         * 
         * @param eventsPerByte
         */
        public PerfHandler(final int eventsPerByte) {
            this("*", eventsPerByte);
        }


        /**
         * Constructor.
         * 
         * @param topic
         * @param eventsPerByte
         */
        public PerfHandler(final String topic, final int eventsPerByte) {
            this.topic = topic;
            this.noReceivedEvents = 0;
            this.eventsPerByte = eventsPerByte;
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

            if (noReceivedEvents == 0) // first time or resetted
                System.out.println("# timestamp, latency, bytes, throughtput");

            ++noReceivedEvents;

            final long now = System.currentTimeMillis();
            final double lat = getLatency(now, e);
            final double throughput = eventsPerByte / lat;

            System.out.println(e.timestamp() + "," + lat + "," + eventsPerByte + "," + throughput);
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
     * @param consumersAddress
     * @param port
     */
    private Consumer(final String consumersAddress, final int port) {
        this.server = new WebServer(port);
        this.registry = new VismoEventRegistry(consumersAddress);
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
     * @param size
     * @return n
     */
    int registerHandler(final int size) {
        final PerfHandler handler = new PerfHandler(size);

        registry.registerToAll(handler);
        handlers.add(handler);

        return handlers.size() - 1;
    }


    /**
     * @param topic
     * @param size
     * @return n
     */
    int registerHandler(final String topic, final int size) {
        final PerfHandler handler = new PerfHandler(topic, size);

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
            System.err.println(PROG + " config-file [port:-" + PORT + "]");
            System.exit(1);
        }

        final String configFile = args[0];
        final int port = args.length == 2 ? Integer.valueOf(args[1]) : PORT;

        requireFile(configFile);
        final VismoConfiguration conf = new VismoConfiguration(configFile);
        final String consumersAddress = "tcp://" + conf.getClusterHead() + ":" + conf.getConsumersPort();

        new Consumer(consumersAddress, port).start();
    }
}
