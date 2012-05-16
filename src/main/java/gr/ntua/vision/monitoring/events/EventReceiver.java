package gr.ntua.vision.monitoring.events;

import gr.ntua.vision.monitoring.StoppableTask;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 * This is used to collect events from the various services.
 */
public class EventReceiver extends StoppableTask {
    /***/
    private final ZContext            ctx;
    /***/
    private final String              eventsEndPoint;
    /** the event factory. */
    private final EventFactory        factory      = new EventFactory();
    /***/
    private final List<EventListener> listeners    = new ArrayList<EventListener>();
    /***/
    private final Logger              log          = LoggerFactory.getLogger(EventReceiver.class);
    /***/
    private final Socket              sock;
    /***/
    private final String              STOP_MESSAGE = "stop!";


    /**
     * Constructor.
     * 
     * @param ctx
     * @param eventsEndPoint
     */
    public EventReceiver(final ZContext ctx, final String eventsEndPoint) {
        super("event-receiver");
        this.ctx = ctx;
        this.eventsEndPoint = eventsEndPoint;
        this.sock = ctx.createSocket(ZMQ.PULL);
        this.sock.bind(eventsEndPoint);
        this.sock.setLinger(0);
        log.debug("listening on endpoint={}", eventsEndPoint);
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        log.debug("ready to pull");

        while (true) {
            final String msg = receive(sock);

            if (msg == null) {
                log.error("receiving null event");
                continue;
            }

            log.trace("received: {}", msg);

            if (msg.equals(STOP_MESSAGE))
                break;

            final Event e = factory.createEvent(msg);

            if (e != null)
                notifyWith(e);
        }

        log.debug("shutting down");
    }


    /**
     * Stop the thread. This call is guaranteed to promptly interrupt <code>this</code> thread execution.
     * 
     * @see gr.ntua.vision.monitoring.StoppableTask#shutDown()
     */
    @Override
    public void shutDown() {
        super.shutDown();
        sendStopMessage();
    }


    /**
     * Subscribe a new listener. There is no guarantee in the order the listeners will get notified.
     * 
     * @param listener
     *            the listener to add.
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
    private void notifyWith(final Event e) {
        for (final EventListener listener : listeners)
            listener.notify(e);
    }


    /**
     * Ask the thread to stop receiving messages.
     */
    private void sendStopMessage() {
        final Socket stopSock = ctx.createSocket(ZMQ.PUSH);

        stopSock.connect(eventsEndPoint);
        stopSock.send(STOP_MESSAGE.getBytes(), 0);
        stopSock.close();
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
