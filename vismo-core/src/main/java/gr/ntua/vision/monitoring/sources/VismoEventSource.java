package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.sockets.Socket;
import gr.ntua.vision.monitoring.threading.StoppableTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class VismoEventSource extends StoppableTask implements EventSource {
    /***/
    private static final Pattern      patt     = Pattern.compile("\"originating-machine\": ?\"([^\"]*)\"");
    /***/
    private static final String       SHUTDOWN = "shutdown!";
    /** the socket used to receive events from outside the system. */
    private final Socket              eventSock;
    /***/
    private final EventFactory        factory  = new VismoEventFactory();
    /***/
    private final EventSourceListener listener;
    /** the log target. */
    private final Logger              log      = LoggerFactory.getLogger(VismoEventSource.class);
    /** this is used to shutdown the thread. */
    private final Socket              shutdownSocket;


    /**
     * Constructor.
     * 
     * @param listener
     * @param eventSock
     *            the socket used to receive events from outside the system.
     * @param shutdownSocket
     *            this is used to shutdown the thread.
     */
    public VismoEventSource(final EventSourceListener listener, final Socket eventSock, final Socket shutdownSocket) {
        super("vismo-event-source");
        this.listener = listener;
        this.eventSock = eventSock;
        this.shutdownSocket = shutdownSocket;
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
                final MonitoringEvent e = factory.createEvent(message);

                listener.receive(e);
            } catch (final Throwable x) {
                log.error("deserialization error", x);
                log.debug("skipping");
            }
        }

        shutdownSocket.close();
        eventSock.close();
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
