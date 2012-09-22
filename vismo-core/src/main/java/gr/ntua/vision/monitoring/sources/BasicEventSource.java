package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.EventListener;
import gr.ntua.vision.monitoring.StoppableTask;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.zmq.VismoSocket;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class BasicEventSource extends StoppableTask implements EventSource {
    /***/
    private final EventFactory             factory;
    /** the listeners lists. */
    private final ArrayList<EventListener> listeners = new ArrayList<EventListener>();
    /** the log target. */
    private final Logger                   log       = LoggerFactory.getLogger(BasicEventSource.class);
    /***/
    private final VismoSocket              sock;


    /**
     * Constructor.
     * 
     * @param sock
     *            the socket used to receive events from outside the system.
     * @param factory
     */
    public BasicEventSource(final EventFactory factory, final VismoSocket sock) {
        super("basic-event-source");
        this.factory = factory;
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

            log.trace("received: {}", message);

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
     * Subscribe a new listener. There is no guarantee in the order the listeners will be notified.
     * 
     * @param listener
     *            the listener to subscribe.
     */
    @Override
    public void subscribe(final EventListener listener) {
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
        for (final EventListener listener : listeners)
            listener.receive(e);
    }
}
