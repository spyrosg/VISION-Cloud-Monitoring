package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.threading.StoppableTask;
import gr.ntua.vision.monitoring.zmq.VismoSocket;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class VismoEventSource extends StoppableTask implements EventSource {
    /***/
    private static final Pattern                 patt      = Pattern.compile("\"originating-machine\": ?\"([^\"]*)\"");
    /***/
    private static final String                  SHUTDOWN  = "shutdown!";
    /** the socket used to receive events from outside the system. */
    private final VismoSocket                    eventSock;
    /***/
    private final EventFactory                   factory   = new VismoEventFactory();
    /** the listeners lists. */
    private final ArrayList<EventSourceListener> listeners = new ArrayList<EventSourceListener>();
    /** the log target. */
    private final Logger                         log       = LoggerFactory.getLogger(VismoEventSource.class);
    /** this is used to shutdown the thread. */
    private final VismoSocket                    shutdownSocket;


    /**
     * Constructor.
     * 
     * @param eventSock
     *            the socket used to receive events from outside the system.
     * @param shutdownSocket
     *            this is used to shutdown the thread.
     */
    public VismoEventSource(final VismoSocket eventSock, final VismoSocket shutdownSocket) {
        super("vismo-event-source");
        this.eventSock = eventSock;
        this.shutdownSocket = shutdownSocket;
    }


    /**
     * @see gr.ntua.vision.monitoring.sources.EventSource#add(gr.ntua.vision.monitoring.sources.EventSourceListener)
     */
    @Override
    public void add(final EventSourceListener listener) {
        listeners.add(listener);
    }


    /**
     * Since zmq sockets are not interruptible, we use another socket to send the stop message to <code>this</code>. This is
     * guaranteed to stop the thread.
     * 
     * @see gr.ntua.vision.monitoring.threading.StoppableTask#halt()
     */
    @Override
    public void halt() {
        shutdownSocket.send(SHUTDOWN);
    }


    /**
     * Receive and dispatch events.
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        log.debug("ready - awaiting events");

        while (!isInterrupted()) {
            final String message = eventSock.receive();

            log.trace("from {}, received {}", getEventSource(message), message);

            if (message == null)
                continue;
            if (SHUTDOWN.equals(message))
                break;

            try {
                final Event e = factory.createEvent(message);

                notifyAll(e);
            } catch (final Throwable x) {
                log.error("deserializing error", x);
                log.debug("skipping");
            }
        }

        log.debug("shutting down");
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<" + getClass().getSimpleName() + " using " + eventSock + ">";
    }


    /**
     * Notify any listeners of the incoming message.
     * 
     * @param e
     *            the event received.
     */
    private void notifyAll(final Event e) {
        for (final EventSourceListener listener : listeners)
            listener.receive(e);
    }


    /**
     * @param eventStr
     * @return the originating machine ip of the event.
     */
    private static String getEventSource(final String eventStr) {
        final Matcher m = patt.matcher(eventStr);

        if (m.find())
            return m.group(1);

        return null;
    }
}
