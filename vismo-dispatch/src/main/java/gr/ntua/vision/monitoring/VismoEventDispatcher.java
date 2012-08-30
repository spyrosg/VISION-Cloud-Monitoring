package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.io.IOException;
import java.net.SocketException;

import org.zeromq.ZContext;


/**
 * 
 */
public class VismoEventDispatcher extends EventDispatcher {
    /***/
    private static VismoConfiguration conf;
    /***/
    private static final String       VISMO_CONFIG_RESOURCE        = "/config.properties";
    /***/
    private static final String       VISMO_CONFIG_SYSTEM_PROPERTY = "vismo.config.properties";
    /***/
    private static final ZMQSockets   zmq                          = new ZMQSockets(new ZContext());

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
        super(zmq.newConnectedPushSocket(conf.getProducersPoint()), serviceName);
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
