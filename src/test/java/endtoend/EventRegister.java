package endtoend;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.EventHandler;

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
 *
 */
public class EventRegister extends Thread {
    /** the log target. */
    private static final Logger                   log                = LoggerFactory.getLogger(EventRegister.class);
    /***/
    private final EventFactory                    factory            = new EventFactory();
    /** the list of event handlers, per topic. */
    private final Map<String, List<EventHandler>> registeredHandlers = new HashMap<String, List<EventHandler>>();
    /***/
    private final Socket                          sock;


    /**
     * Constructor
     * 
     * @param ctx
     *            the zmq context.
     * @param distributionEventsPort
     */
    public EventRegister(final ZContext ctx, final String distributionEventsPort) {
        super("event-registration");
        this.sock = ctx.createSocket(ZMQ.PULL);
        this.sock.setLinger(0);
        this.sock.connect(distributionEventsPort);
        log.info("connecting to {}", distributionEventsPort);
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
        log.trace("registering {} for topic '{}'", handler, topic);

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
                notify(e);
        }
    }


    /**
     * @return
     */
    private int countRegisteredHandlers() {
        int sum = 0;

        for (final List<EventHandler> handlerList : registeredHandlers.values())
            sum += handlerList.size();

        return sum;
    }


    /**
     * @param topic
     * @return
     */
    private List<EventHandler> getTopicList(final String topic) {
        final List<EventHandler> list = registeredHandlers.get(topic);

        return list != null ? list : new ArrayList<EventHandler>();
    }


    /**
     * @param e
     */
    private void notify(final Event e) {
        // FIXME: for now, push all events to all handlers

        log.trace("about to notify {} handlers", countRegisteredHandlers());

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
