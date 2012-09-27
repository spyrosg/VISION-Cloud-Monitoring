package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.zmq.VismoSocket;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.io.IOException;
import java.net.SocketException;
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
    /** the resource file used when no configuration file is passed by the client. */
    private static final String       VISMO_CONFIG_RESOURCE        = "/config.properties";
    /** the property used to denote the location of the configuration file. */
    private static final String       VISMO_CONFIG_SYSTEM_PROPERTY = "vismo.config.properties";
    /** the zmq object. */
    private static final ZMQSockets   zmq                          = new ZMQSockets(new ZContext());
    /** the event builder. */
    private final EventBuilder        builder;
    /** the machine's external ip address. */
    private final String              ip;
    /** the name of the service that generate events. */
    private final String              originatingService;
    /** the socket to use. */
    private final VismoSocket         sock;

    static {
        try {
            loadConfiguration();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Constructor.
     * 
     * @param serviceName
     *            the name of the service generating the events.
     * @throws SocketException
     */
    public VismoEventDispatcher(final String serviceName) throws SocketException {
        this(serviceName, zmq.newConnectedPushSocket(conf.getProducersPoint()));
    }


    /**
     * Constructor.
     * 
     * @param serviceName
     *            the name of the service generating the events.
     * @param sock
     *            the socket to use.
     * @throws SocketException
     */
    public VismoEventDispatcher(final String serviceName, final VismoSocket sock) throws SocketException {
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
     * Send the event to the locally running <code>vismo</code> instance.
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
     * FIXME: get cluster from configuration
     * 
     * @return the name of the cluster this machines belongs to.
     */
    @SuppressWarnings("static-method")
    private String getOriginatingCluster() {
        return "test-1";
    }


    /**
     * @return an id for the event.
     */
    private static String getEventId() {
        return UUID.randomUUID().toString();
    }


    /**
     * Try to load the configuration. First try reading the file specified in the system property; if the property is null, try
     * loading the configuration from inside the jar.
     * 
     * @throws IOException
     */
    private static void loadConfiguration() throws IOException {
        final String configFile = System.getProperty(VISMO_CONFIG_SYSTEM_PROPERTY);

        if (configFile != null) {
            conf = new VismoConfiguration(configFile);
            return;
        }

        conf = VismoConfiguration.loadFromResource(VismoEventDispatcher.class.getResourceAsStream((VISMO_CONFIG_RESOURCE)));
    }
}
