package gr.ntua.vision.monitoring.notify;

import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.sockets.Socket;

import java.util.logging.Logger;


/**
 * The event handler task is responsible for listening for incoming events and pass those events to the handler.
 */
public class EventHandlerTask implements Runnable {
    /** the log target. */
    private static final Logger ilog               = Logger.getLogger(EventHandlerTask.class.getName());
    /** no of milliseconds to sleep for when receiving an event. */
    private static final int    OPT_SLEEP_DURATION = 50;
    /** the event factory. */
    private final EventFactory  factory;
    /** the actual handler. */
    private final EventHandler  handler;
    /** the socket to receive messages. */
    private final Socket        sock;
    /***/
    private volatile boolean    stop;


    /**
     * Constructor.
     * 
     * @param factory
     *            the event factory.
     * @param sock
     *            the socket to receive messages.
     * @param handler
     *            the actual handler.
     */
    EventHandlerTask(final EventFactory factory, final Socket sock, final EventHandler handler) {
        this.factory = factory;
        this.sock = sock;
        this.handler = handler;
        this.stop = false;
    }


    /**
     * Halt the task's execution.
     */
    public void halt() {
        stop = true;
    }


    /**
     * @return <code>true</code> iff the runnable is still in the receive loop, <code>false</code> otherwise.
     */
    public boolean isRunning() {
        return !stop;
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        ilog.config("entering receive/handle loop");

        while (!Thread.currentThread().isInterrupted() && !stop) {
            final String topicAndMessage = sock.receiveNonBlocking();

            if (topicAndMessage == null) {
                try {
                    Thread.sleep(OPT_SLEEP_DURATION);
                } catch (final InterruptedException ignored) {
                    // NOP
                }

                continue;
            }

            ilog.fine("received: " + topicAndMessage);

            final String message = extractMessage(topicAndMessage);

            if (message == null) {
                ilog.warning("received event without topic; ignoring");
                continue;
            }

            final MonitoringEvent e = factory.createEvent(message);

            if (e != null)
                try {
                    handler.handle(e);
                } catch (final Throwable x) {
                    ilog.warning("exception: " + x.getMessage());
                    x.printStackTrace();
                }
        }

        ilog.config("leaving loop");
        sock.close();
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<EventHandlerTask: " + sock + ">";
    }


    /**
     * Bypass the topic, return the actual message.
     * 
     * @param s
     * @return the message part of the received string, or <code>null</code> if there's no topic.
     */
    private static String extractMessage(final String s) {
        final int topicIndex = s.indexOf(" ");

        if (topicIndex < 0)
            return null;

        return s.substring(topicIndex + 1);
    }
}
