package gr.ntua.vision.monitoring.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 * The event registry is the mechanism through which an event consumer is notified of new events.
 */
public class EventRegistry extends Thread {
    /** the log target. */
    private static final Logger                   log                = LoggerFactory.getLogger(EventRegistry.class);
    /***/
    private static final byte[]                   TO_ALL             = "".getBytes();
    /** the event factory. */
    private final EventFactory                    factory            = new EventFactory();
    /** the list of event handlers, per topic. */
    private final Map<String, List<EventHandler>> registeredHandlers = new HashMap<String, List<EventHandler>>();
    /** the socket. */
    private final Socket                          sock;


    /**
     * Constructor.
     * 
     * @param ctx
     *            the zmq context.
     * @param distributionEventsPort
     *            the zmq port in which events arrive from the main vismo component.
     */
    public EventRegistry(final ZContext ctx, final String distributionEventsPort) {
        super("event-registration");
        this.sock = ctx.createSocket(ZMQ.SUB);
        this.sock.setLinger(0);
        this.sock.connect(distributionEventsPort);
        this.sock.subscribe(TO_ALL);
        log.debug("connecting to endpoint={}", distributionEventsPort);
    }


    /**
     * Register the handler to receive events only of the given topic.
     * 
     * @param topic
     *            the event topic.
     * @param handler
     *            the handler.
     */
    public void register(final String topic, final EventHandler handler) {
        log.debug("registering {} for topic '{}'", handler, topic);

        final List<EventHandler> handlers = registeredHandlers.get(topic);

        if (handlers != null) {
            handlers.add(handler);
            return;
        }

        final List<EventHandler> newHandlerList = new ArrayList<EventHandler>();

        newHandlerList.add(handler);
        registeredHandlers.put(topic, newHandlerList);
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        log.debug("entering receive/notify loop");

        while (!isInterrupted()) {
            final String msg = receive(sock);

            if (msg == null)
                continue;

            log.trace("received: {}", msg);

            final Event e = factory.createEvent(msg);

            if (e != null)
                notifyOf(e);
        }
    }


    /**
     * @param e
     */
    private void notifyOf(final Event e) {
        // FIXME: for now, push all events to all handlers

        for (final List<EventHandler> handlerList : registeredHandlers.values())
            for (final EventHandler handler : handlerList) {
                log.trace("notifying consumer handler {} on event {}", handler, e);
                handler.handler(e);
            }
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
