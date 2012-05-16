package gr.ntua.vision.monitoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 * This is used to collect events from the various services.
 */
public class EventReceiver extends Thread {
    /***/
    private final List<EventListener> listeners    = new ArrayList<EventListener>();
    /***/
    private final Logger              log          = LoggerFactory.getLogger(EventReceiver.class);
    /***/
    private final JSONParser          parser       = new JSONParser();
    /***/
    private final Socket              sock;
    /***/
    private final String              STOP_MESSAGE = "STOP!";


    /**
     * Constructor.
     * 
     * @param ctx
     * @param eventsEndPoint
     */
    public EventReceiver(final ZContext ctx, final String eventsEndPoint) {
        this.sock = ctx.createSocket(ZMQ.PULL);
        this.sock.bind(eventsEndPoint);
        this.sock.setLinger(0);
        log.debug("listening on endpoint={}", eventsEndPoint);
    }


    /**
     * @param listener
     */
    public void add(final EventListener listener) {
        listeners.add(listener);
    }


    /**
     * 
     */
    @Override
    public void run() {
        log.debug("entering receive loop");

        while (!isInterrupted()) {
            final byte[] buf = sock.recv(0);

            if (buf == null) {
                log.error("receiving null event");
                continue;
            }

            final String msg = new String(buf, 0, buf.length);

            if (msg.equals(STOP_MESSAGE))
                break;

            log.trace("received: {}", msg);

            @SuppressWarnings("rawtypes")
            final Map dict = parse(msg);

            if (dict != null)
                notifyWith(new DummyEvent(dict));
        }

        log.debug("shutting down");
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
     * De-serialize the message as a json object.
     * 
     * @param msg
     *            the message string.
     * @return if successful, return a java {@link Map} representing the json object, <code>null</code> otherwise.
     */
    @SuppressWarnings("rawtypes")
    private Map parse(final String msg) {
        try {
            return (Map) parser.parse(msg);
        } catch (final ParseException e) {
            log.error("error deserializing: {}", msg);
            log.error("ParseException", e);

            return null;
        }
    }
}
