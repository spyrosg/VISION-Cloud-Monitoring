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
    private final String                          eventsEndPoint     = "tcp://localhost:27890";
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
     */
    public EventRegister(final ZContext ctx) {
        super("event-registration");
        log.info("starting event registration");
        this.sock = ctx.createSocket(ZMQ.PULL);
        this.sock.setLinger(0);
        this.sock.connect(eventsEndPoint);
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
        getTopicList(topic).add(handler);
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        while (!isInterrupted()) {
            final byte[] buf = sock.recv(0);

            if (buf == null) {
                log.error("null message received");
                continue;
            }

            final String msg = new String(buf, 0, buf.length);
            final Event e = factory.createEvent(msg);

            if (e != null)
                notify(e);
        }
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

        for (final List<EventHandler> handlerList : registeredHandlers.values())
            for (final EventHandler handler : handlerList)
                handler.handler(e);
    }
}
