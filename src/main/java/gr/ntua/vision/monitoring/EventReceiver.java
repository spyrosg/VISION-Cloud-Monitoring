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
public class EventReceiver extends StoppableTask {
    /***/
    private final ZContext            ctx;
    /***/
    private final String              eventsEndPoint;
    /***/
    private final List<EventListener> listeners    = new ArrayList<EventListener>();
    /***/
    private final Logger              log          = LoggerFactory.getLogger(EventReceiver.class);
    /***/
    private final JSONParser          parser       = new JSONParser();
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

            @SuppressWarnings("rawtypes")
            final Map dict = parse(msg);

            if (dict != null)
                notifyWith(new DummyEvent(dict));
        }

        log.debug("shutting down");
    }


    /**
     * @see gr.ntua.vision.monitoring.StoppableTask#shutDown()
     */
    @Override
    public void shutDown() {
        super.shutDown();
        final Socket stopSock = ctx.createSocket(ZMQ.PUSH);

        stopSock.connect(eventsEndPoint);
        stopSock.send(STOP_MESSAGE.getBytes(), 0);
        stopSock.close();
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


    /**
     * @param sock
     * @return
     */
    private static String receive(final Socket sock) {
        final byte[] buf = sock.recv(0);

        if (buf == null)
            return null;

        return new String(buf, 0, buf.length);
    }
}
