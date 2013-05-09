package gr.ntua.vision.monitoring.notify;

import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.sockets.Socket;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
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
    private static final Logger               log               = Logger.getLogger(EventRegistry.class.getName());
    /** the property name to set when activating logging output. */
    private static final String               notifyLogProperty = "notify.log";
    /** the address all consumers will connect to. */
    private final String                      addr;
    /** the socket factory. */
    private final ZMQFactory                  socketFactory;
    /***/
    private final ArrayList<EventHandlerTask> tasks             = new ArrayList<EventHandlerTask>();
    /** the pool of threads. Each thread corresponds to one event handler. */
    private final ArrayList<Thread>           threads           = new ArrayList<Thread>();

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
    protected EventRegistry(final ZMQFactory socketFactory, final String addr) {
        this.socketFactory = socketFactory;
        this.addr = addr;
    }


    /**
     * This should be called out the end of the application. Stop receiving any events; halt any running event handlers; don't
     * accept other registrations.
     */
    public void halt() {
        log.config("halting " + tasks.size() + " tasks");

        for (final EventHandlerTask t : tasks)
            t.halt();

        log.config("joining " + threads.size() + " threads");

        for (final Thread t : threads)
            try {
                t.join();
            } catch (final InterruptedException ignored) {
                // NOP
            }

        log.config("joined");
    }


    /**
     * Register the handler to receive events only of the given topic.
     * 
     * @param topic
     *            the event topic.
     * @param handler
     *            the handler.
     * @return the {@link EventHandlerTask} for the given handler.
     */
    public EventHandlerTask register(final String topic, final EventHandler handler) {
        final Socket sock = socketFactory.newSubSocket(addr, topic);
        final EventHandlerTask task = new EventHandlerTask(new VismoEventFactory(), sock, handler);

        log.config("registering handler for topic '" + topic + "' => " + task);

        return start(task);
    }


    /**
     * Register the handler to receive events from all topics.
     * 
     * @param handler
     *            the handler.
     * @return the {@link EventHandlerTask} for the given handler.
     */
    public EventHandlerTask registerToAll(final EventHandler handler) {
        return register("", handler);
    }


    /**
     * Start a new thread, running the task.
     * 
     * @param task
     * @return the <code>task</code>.
     */
    private EventHandlerTask start(final EventHandlerTask task) {
        final Thread t = new Thread(task, task.toString());

        threads.add(t);
        tasks.add(task);
        t.start();

        return task;
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
