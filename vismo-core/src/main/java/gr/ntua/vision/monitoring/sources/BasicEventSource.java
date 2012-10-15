package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.EventSourceListener;
import gr.ntua.vision.monitoring.StoppableTask;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.zmq.VismoSocket;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class BasicEventSource extends StoppableTask implements EventSource {
    /***/
    private static final Pattern                 patt      = Pattern.compile("\"originating-machine\": ?\"([^\"]*)\"");
    /***/
    private final EventFactory                   factory   = new VismoEventFactory();
    /** the listeners lists. */
    private final ArrayList<EventSourceListener> listeners = new ArrayList<EventSourceListener>();
    /** the log target. */
    private final Logger                         log       = LoggerFactory.getLogger(BasicEventSource.class);
    /***/
    private final VismoSocket                    sock;


    /**
     * Constructor.
     * 
     * @param sock
     *            the socket used to receive events from outside the system.
     */
    public BasicEventSource(final VismoSocket sock) {
        super("basic-event-source");
        this.sock = sock;
    }


    /**
     * Receive and dispatch events.
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        log.debug("ready - awaiting events");

        while (true) {
            final String message = sock.receive();

            log.trace("from {}, received {}", getEventSource(message), message);

            if (message == null)
                continue;

            try {
                final Event e = factory.createEvent(message);

                notifyAll(e);
            } catch (final Throwable x) {
                log.error("deserializing error", x);
                log.debug("skipping");
            }
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.StoppableTask#shutDown()
     */
    @Override
    public void shutDown() {
        try {
            interrupt();
            sock.close();
        } catch (final Throwable x) {
            // ignored
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.sources.EventSource#subscribe(gr.ntua.vision.monitoring.EventSourceListener)
     */
    @Override
    public void subscribe(final EventSourceListener listener) {
        log.debug("subscribing listener {}", listener);
        listeners.add(listener);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<BasicEventSource: using " + sock + ">";
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
