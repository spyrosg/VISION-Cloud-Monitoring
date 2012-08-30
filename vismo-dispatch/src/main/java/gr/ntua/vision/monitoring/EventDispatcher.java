package gr.ntua.vision.monitoring;

import java.net.SocketException;
import java.util.Map;

import org.json.simple.JSONValue;
import org.zeromq.ZMQ.Socket;


/**
 * 
 */
public class EventDispatcher {
    /** the machine's external ip address. */
    private final String ip;
    /** the name of the service that generate events. */
    private final String originatingService;
    /** the socket to use. */
    private final Socket sock;


    /**
     * Constructor.
     * 
     * @param sock
     *            the socket to use.
     * @param serviceName
     *            the name of the service that generate events.
     * @throws SocketException
     */
    public EventDispatcher(final Socket sock, final String serviceName) throws SocketException {
        this.sock = sock;
        this.originatingService = serviceName;
        this.ip = new VismoVMInfo().getAddress().getHostAddress();
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
