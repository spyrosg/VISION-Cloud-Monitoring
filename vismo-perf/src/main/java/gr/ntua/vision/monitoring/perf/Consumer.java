package gr.ntua.vision.monitoring.perf;

import static gr.ntua.vision.monitoring.perf.Utils.requireFile;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.EventHandlerTask;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 
 */
public class Consumer {
    /**
     * 
     */
    private static class PerfHandler implements EventHandler {
        /***/
        private int noReceivedEvents;


        /**
         * Constructor.
         */
        public PerfHandler() {
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
        }
    }

    /***/
    private static final String      PROG = "Consumer";
    /***/
    private PerfHandler              handler;
    /***/
    private final VismoEventRegistry registry;
    /***/
    private EventHandlerTask         task;
    /***/
    private final String             topic;


    /**
     * Constructor.
     * 
     * @param configFile
     */
    private Consumer(final String configFile) {
        this(configFile, null);
    }


    /**
     * Constructor.
     * 
     * @param configFile
     * @param topic
     */
    private Consumer(final String configFile, final String topic) {
        this.registry = new VismoEventRegistry(configFile);
        this.topic = topic;
    }


    /**
     * @return the number of received events.
     */
    protected int getNoReceivedEvents() {
        return (handler != null) ? handler.getNoReceivedEvents() : 0;
    }


    /**
     * 
     */
    protected void halt() {
        if (task != null)
            task.halt();

        registry.halt();
    }


    /**
     * 
     */
    private void start() {
        this.handler = new PerfHandler();
        this.task = topic != null ? registry.register(topic, handler) : registry.registerToAll(handler);
    }


    /**
     * @param args
     */
    public static void main(final String... args) {
        if (args.length < 3) {
            System.err.println("arg count");
            System.err.println(PROG + " config timeout no-events [topic]");
            System.exit(1);
        }

        final String configFile = args[0];
        final long timeout = Long.valueOf(args[1]);
        final int noExpectedEvents = Integer.valueOf(args[2]);

        requireFile(configFile);

        final Timer t = new Timer(true);
        final Consumer cons = args.length == 4 ? new Consumer(configFile, args[3]) : new Consumer(configFile);

        t.schedule(new TimerTask() {
            @Override
            public void run() {
                cons.halt();

                if (noExpectedEvents != cons.getNoReceivedEvents())
                    throw new Error("got " + cons.getNoReceivedEvents() + " events, expecting " + noExpectedEvents);
            }
        }, timeout);

        cons.start();
    }
}
