package gr.ntua.vision.monitoring.notify;

import gr.ntua.vision.monitoring.VismoFormatter;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.sockets.Socket;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The event registry is the mechanism through which an event consumer is notified of new events. The user is expected to register
 * one or more event handlers, each for one the topics of interest. A thread pool is maintained, such that each handler gets its
 * own thread, without blocking the rest of the application or other handlers. This also means that the event handlers will be
 * notified asynchronously to the main client program loop.
 */
class EventRegistry implements Registry {
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
     * @see gr.ntua.vision.monitoring.notify.Registry#halt()
     */
    @Override
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
     * @see gr.ntua.vision.monitoring.notify.Registry#register(java.lang.String, gr.ntua.vision.monitoring.notify.EventHandler)
     */
    @Override
    public EventHandlerTask register(final String topic, final EventHandler handler) {
        final Socket sock = socketFactory.newSubSocket(addr, topic);
        final EventHandlerTask task = new EventHandlerTask(new VismoEventFactory(), sock, handler);

        log.config("registering handler for topic '" + topic + "' => " + task);

        return start(task);
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.Registry#registerToAll(gr.ntua.vision.monitoring.notify.EventHandler)
     */
    @Override
    public EventHandlerTask registerToAll(final EventHandler handler) {
        return register("", handler);
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.Registry#unregister(gr.ntua.vision.monitoring.notify.EventHandler)
     */
    @Override
    public void unregister(final EventHandler handler) {
        for (int i = 0; i < tasks.size(); ++i)
            if (tasks.get(i).handler == handler) {
                tasks.get(i).halt();
                break;
            }
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

        h.setFormatter(new VismoFormatter());
        h.setLevel(Level.ALL);
        Logger.getLogger(pkg).addHandler(h);
        Logger.getLogger(pkg).setLevel(Level.ALL);
    }
}
