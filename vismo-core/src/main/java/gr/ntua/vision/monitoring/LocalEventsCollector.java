package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.VismoEventFactory;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ.Socket;


/**
 * An event collector is the main entry point of the various events happening in localhost, that we care to monitor. This is used
 * to collect these events and pass them around to the rest of the system. Upon event receipt, the interested parties we'll be
 * notified.
 */
public class LocalEventsCollector extends StoppableTask {
    /** the event factory. */
    private final VismoEventFactory   factory      = new VismoEventFactory();
    /** the listeners lists. */
    private final List<EventListener> listeners    = new ArrayList<EventListener>();
    /** the log target. */
    private final Logger              log          = LoggerFactory.getLogger(LocalEventsCollector.class);
    /** the socket used to receive events. */
    private final Socket              receiveEventsSock;
    /** the socket used to send messages. */
    private final Socket              sendMessagesSock;
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
    LocalEventsCollector(final Socket receiveEventsSock, final Socket sendMessagesSock) {
        super("event-receiver");
        this.receiveEventsSock = receiveEventsSock;
        this.sendMessagesSock = sendMessagesSock;
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        log.debug("ready to pull");

        while (true) {
            final String msg = receive(receiveEventsSock);

            if (msg == null)
                continue;

            log.trace("received: {}", msg);

            if (msg.equals(STOP_MESSAGE))
                break;

            final Event e = factory.createEvent(msg);

            if (e != null)
                notifyOf(e);
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
     * Notify any listeners of the incoming event.
     * 
     * @param e
     *            the event.
     */
    private void notifyOf(final Event e) {
        for (final EventListener listener : listeners)
            listener.notify(e);
    }


    /**
     * Ask the thread to stop receiving messages.
     */
    private void sendStopMessage() {
        sendMessagesSock.send(STOP_MESSAGE.getBytes(), 0);
        sendMessagesSock.close();
    }


    /**
     * Receive a new message from the socket.
     * 
     * @param sock
     *            the socket.
     * @return the message as a string, or <code>null</code> if there was an error receiving.
     */
    private static String receive(final Socket sock) {
        final byte[] buf = sock.recv(0);

        if (buf == null)
            return null;

        return new String(buf, 0, buf.length);
    }
}
