package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.zmq.VismoSocket;

import java.util.Map;

import org.json.simple.JSONObject;


/**
 * This is providing utility methods for transmitting an event through a socket.
 */
abstract class AbstractSink implements EventSink {
    /***/
    private final VismoSocket sock;


    /**
     * Constructor.
     * 
     * @param sock
     */
    public AbstractSink(final VismoSocket sock) {
        this.sock = sock;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<AbstractSink: " + sock + ">";
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
        final Map dict = (Map) e.get("!dict");

        return JSONObject.toJSONString(dict);
    }
}
