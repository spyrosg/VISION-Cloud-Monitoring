package gr.ntua.vision.monitoring.dispatch;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoFormatter;
import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.sockets.Socket;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONValue;
import org.zeromq.ZContext;


/**
 * This is the basic implementation of an {@link EventDispatcher}. Under the hood it uses a helper object (
 * {@link VismoEventBuilder}) to provide a fluent interface to sending events.
 */
public class VismoEventDispatcher implements EventDispatcher {
    /***/
    private static final String      dispatchLogProperty = "dispatch.log";
    /** the log target. */
    private static final Logger      log                 = Logger.getLogger(VismoEventDispatcher.class.getName());
    /** the event builder. */
    private final VismoEventBuilder  builder;
    /** the configuration object. */
    private final VismoConfiguration conf;
    /** the machine's external ip address. */
    private final String             ip;
    /** the name of the service that generate events. */
    private final String             originatingService;
    /** the socket to use. */
    private final Socket             sock;

    static {
        activateLogger();
    }


    /**
     * Constructor. In most cases, this is the constructor to use.
     * 
     * @param serviceName
     *            the name of the service generating the events.
     */
    public VismoEventDispatcher(final String serviceName) {
        this(new ZMQFactory(new ZContext()), VismoConfiguration.VISMO_CONFIG_FILE, serviceName);
    }


    /**
     * Constructor.
     * 
     * @param configFile
     *            the vismo configuration file.
     * @param serviceName
     *            the name of the service generating the events.
     */
    public VismoEventDispatcher(final String configFile, final String serviceName) {
        this(new ZMQFactory(new ZContext()), configFile, serviceName);
    }


    /**
     * Constructor.
     * 
     * @param socketFactory
     *            the socket factory.
     * @param configFile
     *            the vismo configuration file.
     * @param serviceName
     *            the name of the service generating the events.
     */
    public VismoEventDispatcher(final ZMQFactory socketFactory, final String configFile, final String serviceName) {
        this(socketFactory, loadConfiguration(configFile), serviceName);
    }


    /**
     * Constructor.
     * 
     * @param socketFactory
     *            the socket factory.
     * @param conf
     *            the configuration object.
     * @param serviceName
     *            the name of the service generating the events.
     */
    public VismoEventDispatcher(final ZMQFactory socketFactory, final VismoConfiguration conf, final String serviceName) {
        this.originatingService = serviceName;
        this.conf = conf;
        this.sock = socketFactory.newConnectedPushSocket(conf.getProducersPoint());
        this.ip = new VismoVMInfo().getAddress().getHostAddress();
        this.builder = new VismoEventBuilder(this);
    }


    /**
     * @see gr.ntua.vision.monitoring.dispatch.EventDispatcher#newEvent()
     */
    @Override
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

        final String s = JSONValue.toJSONString(map);

        log.config("sending event: " + s);
        sock.send(s);
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
     * @return the name of the cluster this machines belongs to.
     */
    private String getOriginatingCluster() {
        return conf.getClusterName();
    }


    /***/
    private static void activateLogger() {
        if (System.getProperty(dispatchLogProperty) == null)
            return;

        final ConsoleHandler h = new ConsoleHandler();
        final String pkg = EventDispatcher.class.getPackage().getName();

        h.setFormatter(new VismoFormatter());
        h.setLevel(Level.ALL);
        Logger.getLogger(pkg).addHandler(h);
        Logger.getLogger(pkg).setLevel(Level.ALL);
    }


    /**
     * @return an id for the event.
     */
    private static String getEventId() {
        return UUID.randomUUID().toString();
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
