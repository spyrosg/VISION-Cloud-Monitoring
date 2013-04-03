package gr.ntua.vision.monitoring.sinks;

import gr.ntua.monitoring.sockets.Socket;
import gr.ntua.vision.monitoring.events.MapBasedEvent;
import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.Map;

import org.json.simple.JSONObject;


/**
 * This is providing utility methods for transmitting an event through a socket.
 */
abstract class AbstractSink implements EventSink {
    /***/
    private volatile boolean closed = false;
    /***/
    private final Socket     sock;


    /**
     * Constructor.
     * 
     * @param sock
     */
    public AbstractSink(final Socket sock) {
        this.sock = sock;
    }


    /**
     * @see gr.ntua.vision.monitoring.sinks.EventSink#close()
     */
    @Override
    public void close() {
        sock.close();
        closed = true;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<" + getClass().getSimpleName() + ": " + sock + ">";
    }


    /**
     * Transmit the string.
     * 
     * @param str
     *            a string.
     */
    protected void send(final String str) {
        if (!closed)
            sock.send(str);
    }


    /**
     * Serialize the event.
     * 
     * @param e
     *            the event.
     * @return a string representation for the event.
     */
    protected static String serialize(final MonitoringEvent e) {
        @SuppressWarnings("rawtypes")
        final Map dict = ((MapBasedEvent) e).dict();

        return JSONObject.toJSONString(dict);
    }
}
