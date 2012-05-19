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
 * An event receiver is the main entry point of the various events happening in localhost, that we care to monitor. This is used
 * to collect these events and pass them around to the rest of the system. Upon event receipt, the interested parties we'll be
 * notified.
 */
public class EventReceiver extends StoppableTask {
    /** the zmq context. */
    private final ZContext            ctx;
    /** the zmq context. */
    private final String              eventsEndPoint;
    /** the event factory. */
    private final EventFactory        factory      = new EventFactory();
    /** the log target. */
    private final Logger              log          = LoggerFactory.getLogger(EventReceiver.class);
    /** the sock receiving events. */
    private final Socket              sock;
    /** the message used to stop the task. */
    private final String              STOP_MESSAGE = "stop!";
    /** the subscribers lists. */
    private final List<EventListener> subscribers  = new ArrayList<EventListener>();


    /**
     * Constructor.
     * 
     * @param ctx
     *            the zmq context.
     * @param eventsEndPoint
     *            the events end-point to bind to.
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
        subscribers.add(listener);
    }


    /**
     * Notify any listeners of the incoming event.
     * 
     * @param e
     *            the event.
     */
    private void notifyWith(final Event e) {
        for (final EventListener listener : subscribers)
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
