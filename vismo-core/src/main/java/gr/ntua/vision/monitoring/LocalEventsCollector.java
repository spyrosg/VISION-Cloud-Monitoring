package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.zmq.VismoSocket;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An event collector is the main entry point of the various events happening in localhost, that we care to monitor. This is used
 * to collect these events and pass them around to the rest of the system. Upon event receipt, the interested parties we'll be
 * notified.
 */
public class LocalEventsCollector extends StoppableTask {
    /** the listeners lists. */
    private final List<EventListener> listeners    = new ArrayList<EventListener>();
    /** the log target. */
    private final Logger              log          = LoggerFactory.getLogger(LocalEventsCollector.class);
    /** the socket used to receive events. */
    private final VismoSocket         receiveEventsSock;
    /** the socket used to send messages. */
    private final VismoSocket         sendMessagesSock;
    /** the message used to stop the task. */
    private final String              STOP_MESSAGE = "stop!";


    /**
     * Constructor.
     * 
     * @param receiveEventsSock
     *            the socket used to receive events.
     * @param sendMessagesSock
     *            the socket used to send messages.
     */
    LocalEventsCollector(final VismoSocket receiveEventsSock, final VismoSocket sendMessagesSock) {
        super("event-receiver");
        log.debug("using {}", receiveEventsSock.toString());
        log.debug("using {}", sendMessagesSock.toString());
        this.receiveEventsSock = receiveEventsSock;
        this.sendMessagesSock = sendMessagesSock;
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        log.debug("ready - awaiting events");

        while (true) {
            final String message = receiveEventsSock.receive();

            if (message == null)
                continue;

            log.trace("received: {}", message);

            if (message.equals(STOP_MESSAGE))
                break;

            notifyAllOf(message);
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
    public void subscribe(final EventListener listener) {
        listeners.add(listener);
    }


    /**
     * Notify any listeners of the incoming message.
     * 
     * @param message
     *            the message.
     */
    private void notifyAllOf(final String message) {
        for (final EventListener listener : listeners)
            listener.notify(message);
    }


    /**
     * Ask the thread to stop receiving messages.
     */
    private void sendStopMessage() {
        sendMessagesSock.send(STOP_MESSAGE);
        sendMessagesSock.close();
    }
}
