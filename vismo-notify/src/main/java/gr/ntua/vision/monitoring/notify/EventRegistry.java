package gr.ntua.vision.monitoring.notify;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.zmq.VismoSocket;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


/**
 * The event registry is the mechanism through which an event consumer is notified of new events. The user is expected to register
 * one or more event handlers, each for one the topics of interest. A thread pool is maintained, such that each handler gets its
 * own thread, without blocking the rest of the application or other handlers. This also means that the event handlers will be
 * notified asynchronously to the main client program loop.
 */
class EventRegistry {
    /**
     * The event handler task is responsible for listening for incoming events and pass those events to the handler.
     */
    private static class EventHandlerTask implements Runnable {
        /** the log target. */
        private static final Logger ilog = Logger.getLogger(EventHandlerTask.class.getName());
        /** the event factory. */
        private final EventFactory  factory;
        /** the actual handler. */
        private final EventHandler  handler;
        /** the socket. */
        private final VismoSocket   sock;


        /**
         * Constructor.
         * 
         * @param factory
         *            the event factory.
         * @param sock
         *            the socket.
         * @param handler
         *            the actual handler.
         */
        public EventHandlerTask(final EventFactory factory, final VismoSocket sock, final EventHandler handler) {
            this.factory = factory;
            this.sock = sock;
            this.handler = handler;
        }


        /**
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            ilog.config("entering receive/handle loop");

            while (true) {
                final String msg = sock.receive();

                ilog.fine("received: " + msg);

                if (msg == null)
                    continue;

                // bypass topic
                final int topicIndex = msg.indexOf(" ");
                final Event e = factory.createEvent(msg.substring(topicIndex + 1));

                if (e != null)
                    try {
                        handler.handle(e);
                    } catch (final Throwable x) {
                        x.printStackTrace();
                    }
            }
        }
    }


    /**
     * A custom log formatter. The format should match the following logback notation:
     * <code>%-5p [%d{ISO8601," + timeZone.getID() + "}] %c: %m\n%ex</code>.
     */
    private static class VisionFormatter extends Formatter {
        // INFO [2012-06-11 10:05:42,525] gr.ntua.vision.monitoring.MonitoringInstance: Starting up, pid=28206, ip=vis0/10.0.0.10
        /***/
        private final DateFormat fmt;


        /**
         * Constructor.
         */
        public VisionFormatter() {
            this.fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            this.fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        }


        /**
         * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
         */
        @Override
        public String format(final LogRecord r) {
            final String s = String.format("%-6s [%s] %s: %s\n", r.getLevel(), fmt.format(new Date(r.getMillis())),
                                           r.getSourceClassName(), r.getMessage());

            if (r.getThrown() != null) {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);

                r.getThrown().printStackTrace(pw);

                return s + sw.toString();
            }

            return s;
        }
    }

    /***/
    private static final Logger   log               = Logger.getLogger(EventRegistry.class.getName());
    /** the property name to set when activating logging output. */
    private static final String   notifyLogProperty = "notify.log";
    /** the address all consumers will connect to. */
    private final String          addr;
    /** the pool of threads. Each thread corresponds to one event handler. */
    private final ExecutorService pool              = Executors.newCachedThreadPool();
    /** the socket factory. */
    private final ZMQFactory      socketFactory;

    static {
        activateLogger();
    }


    /**
     * Constructor.
     * 
     * @param socketFactory
     *            the socket factory.
     * @param addr
     *            the address to connect to for incoming events.
     */
    public EventRegistry(final ZMQFactory socketFactory, final String addr) {
        this.socketFactory = socketFactory;
        this.addr = addr;
    }


    /**
     * Register the handler to receive events only of the given topic.
     * 
     * @param topic
     *            the event topic.
     * @param handler
     *            the handler.
     */
    public void register(final String topic, final EventHandler handler) {
        final VismoSocket sock = socketFactory.newSubSocketForTopic(addr, topic);

        log.config("registering handler for topic '" + topic + "', using " + sock);
        pool.submit(new EventHandlerTask(new VismoEventFactory(), sock, handler));
    }


    /**
     * Register the handler to receive events from all topics.
     * 
     * @param handler
     *            the handler.
     */
    public void registerToAll(final EventHandler handler) {
        register("", handler);
    }


    /***/
    private static void activateLogger() {
        if (System.getProperty(notifyLogProperty) == null)
            return;

        final ConsoleHandler h = new ConsoleHandler();
        final String pkg = EventRegistry.class.getPackage().getName();

        h.setFormatter(new VisionFormatter());
        h.setLevel(Level.ALL);
        Logger.getLogger(pkg).addHandler(h);
        Logger.getLogger(pkg).setLevel(Level.ALL);
    }
}
