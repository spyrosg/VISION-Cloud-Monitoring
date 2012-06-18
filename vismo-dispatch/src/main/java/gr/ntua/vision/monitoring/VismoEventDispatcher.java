package gr.ntua.vision.monitoring;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 *
 */
public class VismoEventDispatcher {
    /**
     * This is a convenience object in generating events.
     */
    public static class EventBuilder {
        /***/
        private final Map<String, Object>  dict = new HashMap<String, Object>();
        /***/
        private final VismoEventDispatcher dispatcher;


        /**
         * @param dispatcher
         */
        public EventBuilder(final VismoEventDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }


        /**
         * @param key
         * @param value
         * @return
         */
        public EventBuilder field(final String key, final Object value) {
            dict.put(key, value);
            return this;
        }


        /**
         * 
         */
        public void send() {
            dispatcher.send(dict);
        }
    }

    /***/
    private final String ip;
    /***/
    private final String originatingService;
    /***/
    private final Socket sock;


    /**
     * @param ctx
     * @param localEventsPort
     * @param serviceName
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
     * @param key
     * @param value
     * @return
     */
    public EventBuilder field(final String key, final Object value) {
        return new EventBuilder(this).field(key, value);
    }


    /**
     * @param map
     */
    public void send(final Map<String, Object> map) {
        map.put("originating-service", originatingService);
        map.put("originating-ip", ip);
        map.put("timestamp", System.currentTimeMillis());
        System.err.println("sending: " + map);
        sock.send(JSONValue.toJSONString(map).getBytes(), 0);
    }
}
