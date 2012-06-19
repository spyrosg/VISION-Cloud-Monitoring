package gr.ntua.vision.monitoring;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 * This object is used to
 */
public class VismoEventDispatcher {
    /**
     * This is a convenience object helping generate events.
     */
    public static class EventBuilder {
        /** this is used to keep track of the event fields. */
        private final Map<String, Object>  dict = new HashMap<String, Object>();
        /** the dispatcher. */
        private final VismoEventDispatcher dispatcher;


        /**
         * Constructor.
         * 
         * @param dispatcher
         *            the dispatcher.
         */
        public EventBuilder(final VismoEventDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }


        /**
         * Append to the current event a new key/value pair.
         * 
         * @param key
         *            the key.
         * @param value
         *            the value.
         * @return <code>this</code>.
         */
        public EventBuilder field(final String key, final Object value) {
            dict.put(key, value);
            return this;
        }


        /**
         * Send the event to the locally running <code>vismo</code> instance.
         */
        public void send() {
            dispatcher.send(dict);
        }
    }

    /** the machine's external ip address. */
    private final String ip;
    /** the name of the service that generate events. */
    private final String originatingService;
    /** the socket to push events. */
    private final Socket sock;


    /**
     * Constructor.
     * 
     * @param ctx
     *            the zqm context.
     * @param localEventsPort
     * @param serviceName
     *            the name of the service that generate events.
     * @throws SocketException
     */
    public VismoEventDispatcher(final ZContext ctx, final String localEventsPort, final String serviceName)
            throws SocketException {
        this.originatingService = serviceName;
        this.sock = ctx.createSocket(ZMQ.PUSH);
        this.sock.connect(localEventsPort);
        this.sock.setLinger(0);
        this.ip = new VismoVMInfo().getAddress().getHostAddress();
        System.err.println("connected to port=" + localEventsPort + ", ip=" + this.ip);
    }


    /**
     * Prepare to send an event. The {@link EventBuilder} object is used to keep track of the event properties.
     * 
     * @return an {@link EventBuilder} object.
     */
    public EventBuilder newEvent() {
        return new EventBuilder(this);
    }


    /**
     * Send the event to the locally running <code>vismo</code> instance.
     * 
     * @param map
     *            the map used to represent the event.
     */
    public void send(final Map<String, Object> map) {
        map.put("originating-service", originatingService);
        map.put("originating-ip", ip);
        map.put("timestamp", System.currentTimeMillis());
        System.err.println("sending: " + map);
        sock.send(JSONValue.toJSONString(map).getBytes(), 0);
    }
}
