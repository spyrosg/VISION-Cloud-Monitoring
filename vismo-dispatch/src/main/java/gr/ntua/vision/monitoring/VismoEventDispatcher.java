package gr.ntua.vision.monitoring;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;
import org.zeromq.ZMQ.Socket;


/**
 *
 */
public class VismoEventDispatcher implements EventDispatcher {
    /**
     * This is a convenience object in generating events.
     */
    public static class EventBuilder {
        /***/
        private final Map<String, Object> dict = new HashMap<String, Object>();
        /***/
        private final EventDispatcher     dispatcher;


        /**
         * @param dispatcher
         */
        public EventBuilder(final EventDispatcher dispatcher) {
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
    private final Socket      sock;
    /***/
    private final String      originatingService;
    /***/
    private final InetAddress ip;


    /**
     * @param serviceName
     */
    public VismoEventDispatcher(final String serviceName) {
        this.originatingService = serviceName;
        this.sock = null;
        this.ip = getInetAddress();
    }


    /**
     * @return
     */
    private InetAddress getInetAddress() {
        // TODO Auto-generated method stub
        return null;
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
     * @see gr.ntua.vision.monitoring.EventDispatcher#send(java.util.Map)
     */
    @Override
    public void send(final Map<String, Object> map) {
        map.put("originating-service", originatingService);
        map.put("originating-ip", ip);
        map.put("timestamp", System.currentTimeMillis());
        sock.send(JSONValue.toJSONString(map).getBytes(), 0);
    }
}
