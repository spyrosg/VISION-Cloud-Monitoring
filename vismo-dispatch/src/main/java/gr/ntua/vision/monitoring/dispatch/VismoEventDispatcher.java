package gr.ntua.vision.monitoring.dispatch;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.zmq.VismoSocket;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONValue;
import org.zeromq.ZContext;


/**
 * This is the basic implementation of an {@link EventDispatcher}. Under the hood it uses a helper object ({@link EventBuilder})
 * to provide a fluent interface to sending events.
 */
public class VismoEventDispatcher implements EventDispatcher {
    /** the configuration object. */
    private static VismoConfiguration conf;
    /** the configuration file. */
    private static final String       VISMO_CONFIG_FILE = "/etc/visioncloud_vismo.conf";
    /** the event builder. */
    private final EventBuilder        builder;
    /** the machine's external ip address. */
    private final String              ip;
    /** the name of the service that generate events. */
    private final String              originatingService;
    /** the socket to use. */
    private final VismoSocket         sock;

    static {
        conf = loadConfiguration(VISMO_CONFIG_FILE);
    }


    /**
     * Constructor.
     * 
     * @param serviceName
     *            the name of the service generating the events.
     */
    public VismoEventDispatcher(final String serviceName) {
        this(serviceName, new ZMQSockets(new ZContext()).newConnectedPushSocket(conf.getProducersPoint()));
    }


    /**
     * Constructor.
     * 
     * @param serviceName
     *            the name of the service generating the events.
     * @param sock
     *            the socket to use.
     */
    public VismoEventDispatcher(final String serviceName, final VismoSocket sock) {
        this.sock = sock;
        this.originatingService = serviceName;
        this.ip = new VismoVMInfo().getAddress().getHostAddress();
        this.builder = new EventBuilder(this);
    }


    /**
     * Prepare to send an event. The {@link EventBuilder} object is used to keep track of the event fields.
     * 
     * @return an {@link EventBuilder} object.
     */
    public EventBuilder newEvent() {
        return builder;
    }


    /**
     * @see gr.ntua.vision.monitoring.dispatch.EventDispatcher#send()
     */
    @Override
    public void send() {
        builder.send();
    }


    /**
     * Actually send the event down the socket.
     * 
     * @param map
     *            the event as represented with a {@link Map}.
     */
    void send(final Map<String, Object> map) {
        addBasicFields(map);
        sock.send(JSONValue.toJSONString(map));
    }


    /**
     * @param map
     *            the event as represented with a {@link Map}.
     */
    private void addBasicFields(final Map<String, Object> map) {
        map.put("timestamp", System.currentTimeMillis());
        map.put("originating-machine", ip);
        map.put("originating-service", originatingService);
        map.put("originating-cluster", getOriginatingCluster());
        map.put("id", getEventId());
    }


    /**
     * @return an id for the event.
     */
    private static String getEventId() {
        return UUID.randomUUID().toString();
    }


    /**
     * @return the name of the cluster this machines belongs to.
     */
    private static String getOriginatingCluster() {
        return conf.getClusterName();
    }


    /**
     * Load the configuration.
     * 
     * @param configFile
     * @return the configuration object.
     */
    private static VismoConfiguration loadConfiguration(final String configFile) {
        try {
            return new VismoConfiguration(configFile);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
