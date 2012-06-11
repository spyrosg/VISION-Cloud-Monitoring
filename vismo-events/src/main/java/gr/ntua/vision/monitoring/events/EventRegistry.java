package gr.ntua.vision.monitoring.events;

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
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 * The event registry is the mechanism through which an event consumer is notified of new events. The user is expected to register
 * one or more event handlers, each for one the topics of interest. A thread pool is maintained, such that each handler gets its
 * own thread, without blocking the rest of the application or other handlers. This also means that the event handlers will be
 * notified asynchronounsly to the main client program loop.
 */
public class EventRegistry {
    /**
     * The event handler task is responsible for listening for incoming events and pass those events to the handler.
     */
    private static class EventHandlerTask implements Runnable {
        /** the log target. */
        private static final Logger ilog    = Logger.getLogger(EventHandlerTask.class.getName());
        /** the event factory. */
        private final EventFactory  factory = new EventFactory();
        /** the actual handler. */
        private final EventHandler  handler;
        /** the zmq socket. */
        private final Socket        sock;


        /**
         * Constructor.
         * 
         * @param sock
         *            the zmq socket.
         * @param handler
         *            the actual handler.
         */
        public EventHandlerTask(final Socket sock, final EventHandler handler) {
            this.sock = sock;
            this.handler = handler;
        }


        /**
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            ilog.config("entering receive/notify loop");

            while (!Thread.currentThread().isInterrupted()) {
                final String msg = receive(sock);

                ilog.fine("received: " + msg);

                if (msg == null)
                    continue;
                if (!msg.startsWith("{")) // it's the topic
                    continue;

                final Event e = factory.createEvent(msg);

                if (e != null)
                    handler.handle(e);
            }
        }


        /**
         * Receive a new message from the socket.
         * 
         * @param sock
         *            the socket.
         * @return the message as a string, or <code>null</code> if there was an error receiving.
         */
        private static String receive(final Socket sock) {
            final byte[] buf = sock.recv(0);

            if (buf == null)
                return null;

            return new String(buf, 0, buf.length);
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
    /** the ilog target. */
    private static final Logger   log  = Logger.getLogger(EventRegistry.class.getName());
    /** the zmq context. */
    private final ZContext        ctx;
    /** the zmq port in which events arrive from the main vismo component. */
    private final String          distributionPort;

    /** the pool of threads. Each thread corresponds to one event handler. */
    private final ExecutorService pool = Executors.newCachedThreadPool();


    /**
     * Constructor.
     * 
     * @param ctx
     *            the zmq context.
     * @param distributionPort
     *            the zmq port in which events arrive from the main vismo component.
     */
    public EventRegistry(final ZContext ctx, final String distributionPort) {
        this(ctx, distributionPort, false);
    }


    /**
     * Constructor.
     * 
     * @param ctx
     *            the zmq context.
     * @param distributionPort
     *            the zmq port in which events arrive from the main vismo component.
     * @param debug
     *            when <code>true</code>, it activates the console logger for this package.
     */
    public EventRegistry(final ZContext ctx, final String distributionPort, final boolean debug) {
        this.ctx = ctx;
        this.distributionPort = distributionPort;

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
        final Socket sock = getPubSocketForTopic(ctx, distributionPort, topic);

        log.config("registering " + handler + " for topic '" + topic + "'");
        pool.submit(new EventHandlerTask(sock, handler));
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


    /**
     * 
     */
    private static void activateLogger() {
        final ConsoleHandler h = new ConsoleHandler();
        h.setFormatter(new VisionFormatter());

        h.setLevel(Level.ALL);
        Logger.getLogger("").addHandler(h);
        Logger.getLogger("").setLevel(Level.ALL);
    }


    /**
     * Create and return a new pub socket, filtering only events of given topic.
     * 
     * @param ctx
     *            the zmq context.
     * @param port
     *            the port to connect to.
     * @param topic
     *            the event topic.
     * @return the zmq socket.
     */
    private static Socket getPubSocketForTopic(final ZContext ctx, final String port, final String topic) {
        final org.zeromq.ZMQ.Socket sock = ctx.createSocket(ZMQ.SUB);

        sock.setLinger(0);
        sock.connect(port);
        sock.subscribe(topic.getBytes());

        return sock;
    }
}
