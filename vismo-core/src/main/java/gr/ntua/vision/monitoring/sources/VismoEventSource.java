package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.EventListener;
import gr.ntua.vision.monitoring.StoppableTask;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.zmq.VismoSocket;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is the main, "official" way events are entered into <em>Vismo</em>. In general, the system can have any number of inputs (
 * {@link EventSource}'s) and any number of outputs ({@link EventSink}'s).
 */
public class VismoEventSource extends StoppableTask implements EventSource {
    /** the socket used to receive events from outside the system. */
    private final VismoSocket              eventsSock;
    /***/
    private final EventFactory             factory;
    /** this socket is for internal use (now used by the system to shutdown the source). */
    private final VismoSocket              internalSock;
    /** the listeners lists. */
    private final ArrayList<EventListener> listeners    = new ArrayList<EventListener>();
    /** the log target. */
    private final Logger                   log          = LoggerFactory.getLogger(VismoEventSource.class);
    /** the message used to stop the task. */
    private final String                   STOP_MESSAGE = "stop!";


    /**
     * Constructor.
     * 
     * @param eventsSock
     *            the socket used to receive events from outside the system.
     * @param internalSock
     *            this socket is for internal use (now used by the system to shutdown the source).
     * @param factory
     */
    VismoEventSource(final EventFactory factory, final VismoSocket eventsSock, final VismoSocket internalSock) {
        super("event-receiver");
        this.factory = factory;
        this.eventsSock = eventsSock;
        this.internalSock = internalSock;
        log.debug("using {} for receiving external events", eventsSock);
        log.debug("using {} for internal coordination", internalSock);
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        log.debug("ready - awaiting events");

        while (true) {
            final String message = eventsSock.receive();

            log.trace("received: {}", message);

            if (message == null)
                continue;

            if (message.equals(STOP_MESSAGE))
                break;

            final Event e = factory.createEvent(message);

            notifyAll(e);
        }

        log.debug("shutting down");
    }


    /**
     * Stop the thread. This call is guaranteed to promptly interrupt <code>this</code> thread's execution.
     * 
     * @see gr.ntua.vision.monitoring.StoppableTask#shutDown()
     */
    @Override
    public void shutDown() {
        sendStopMessage();
    }


    /**
     * Subscribe a new listener. There is no guarantee in the order the listeners will be notified.
     * 
     * @param listener
     *            the listener to subscribe.
     */
    @Override
    public void subscribe(final EventListener listener) {
        log.trace("subscribing listener {}", listener);
        listeners.add(listener);
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


    /**
     * Ask the thread to stop receiving messages.
     */
    private void sendStopMessage() {
        internalSock.send(STOP_MESSAGE);
        internalSock.close();
    }
}
