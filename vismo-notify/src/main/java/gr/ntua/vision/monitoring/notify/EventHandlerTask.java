package gr.ntua.vision.monitoring.notify;

import gr.ntua.monitoring.sockets.Socket;
import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.logging.Logger;


/**
 * The event handler task is responsible for listening for incoming events and pass those events to the handler.
 */
public class EventHandlerTask implements Runnable {
    /** the log target. */
    private static final Logger ilog   = Logger.getLogger(EventHandlerTask.class.getName());
    /***/
    private volatile boolean    closed = false;
    /** the event factory. */
    private final EventFactory  factory;
    /** the actual handler. */
    private final EventHandler  handler;
    /** the socket. */
    private final Socket        sock;


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
    public EventHandlerTask(final EventFactory factory, final Socket sock, final EventHandler handler) {
        this.factory = factory;
        this.sock = sock;
        this.handler = handler;
    }


    /**
     * Halt the handler's execution; dispose the thread's resources.
     */
    public void halt() {
        Thread.currentThread().interrupt();
        sock.close();
        closed = true;
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        ilog.config("entering receive/handle loop");

        while (!Thread.currentThread().isInterrupted()) {
            if (closed)
                break;

            final String msg = sock.receive();

            ilog.fine("received: " + msg);

            if (msg == null)
                continue;

            // bypass topic
            final int topicIndex = msg.indexOf(" ");
            final MonitoringEvent e = factory.createEvent(msg.substring(topicIndex + 1));

            if (e != null)
                try {
                    handler.handle(e);
                } catch (final Throwable x) {
                    x.printStackTrace();
                }
        }
    }
}
