package gr.ntua.vision.monitoring.notify;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.zmq.VismoSocket;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

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

import org.zeromq.ZContext;


/**
 * The event registry is the mechanism through which an event consumer is notified of new events. The user is expected to register
 * one or more event handlers, each for one the topics of interest. A thread pool is maintained, such that each handler gets its
 * own thread, without blocking the rest of the application or other handlers. This also means that the event handlers will be
 * notified asynchronously to the main client program loop.
 */
public class EventRegistry {
    /**
     * The event handler task is responsible for listening for incoming events and pass those events to the handler.
     */
    private static class EventHandlerTask implements Runnable {
        /** the log target. */
        private static final Logger     ilog = Logger.getLogger(EventHandlerTask.class.getName());
        /** the event factory. */
        private final VismoEventFactory factory;
        /** the actual handler. */
        private final EventHandler      handler;
        /** the zmq socket. */
        private final VismoSocket       sock;


        /**
         * Constructor.
         * 
         * @param factory
         *            the event factory.
         * @param sock
         *            the zmq socket.
         * @param handler
         *            the actual handler.
         */
        public EventHandlerTask(final VismoEventFactory factory, final VismoSocket sock, final EventHandler handler) {
            this.factory = factory;
            this.sock = sock;
            this.handler = handler;
            ilog.config("using: " + sock);
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
                    handler.handle(e);
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
    private static boolean        logActivated = false;
    /** the address all consumers will connect to. */
    private final String          addr;
    /** the pool of threads. Each thread corresponds to one event handler. */
    private final ExecutorService pool         = Executors.newCachedThreadPool();
    /** the zmq object. */
    private final ZMQSockets      zmq          = new ZMQSockets(new ZContext());


    /**
     * Constructor.
     * 
     * @param addr
     *            the address to connect to for incoming events.
     */
    public EventRegistry(final String addr) {
        this(addr, false);
    }


    /**
     * Constructor.
     * 
     * @param addr
     *            the address to connect to for incoming events.
     * @param debug
     *            when this is <code>true</code> enable debugging output.
     */
    public EventRegistry(final String addr, final boolean debug) {
        this.addr = addr;

        if (debug)
            activateLogger();
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
        final VismoSocket sock = zmq.newSubSocketForTopic(addr, topic);

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
        if (!logActivated) {
            logActivated = true;

            final ConsoleHandler h = new ConsoleHandler();
            h.setFormatter(new VisionFormatter());

            h.setLevel(Level.ALL);
            Logger.getLogger("").addHandler(h);
            Logger.getLogger("").setLevel(Level.ALL);
        }
    }
}
