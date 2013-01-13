package gr.ntua.vision.monitoring.sinks;

import gr.ntua.monitoring.sockets.Socket;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.VismoEvent;

import java.util.Map;

import org.json.simple.JSONObject;


/**
 * This is providing utility methods for transmitting an event through a socket.
 */
abstract class AbstractSink implements EventSink {
    /***/
    private final Socket sock;


    /**
     * Constructor.
     * 
     * @param sock
     */
    public AbstractSink(final Socket sock) {
        this.sock = sock;
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
        sock.send(str);
    }


    /**
     * Serialize the event.
     * 
     * @param e
     *            the event.
     * @return a string representation for the event.
     */
    protected static String serialize(final Event e) {
        @SuppressWarnings("rawtypes")
        final Map dict = ((VismoEvent) e).dict();

        return JSONObject.toJSONString(dict);
    }
}
