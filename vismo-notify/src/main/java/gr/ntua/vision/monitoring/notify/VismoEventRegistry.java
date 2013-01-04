package gr.ntua.vision.monitoring.notify;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.io.IOException;

import org.zeromq.ZContext;


/**
 *
 */
public class VismoEventRegistry extends EventRegistry {
    /** the configuration object. */
    private static VismoConfiguration conf;
    /** the configuration file. */
    private static final String       VISMO_CONFIG_FILE = "/etc/visioncloud_vismo.conf";

    static {
        conf = loadConfiguration(VISMO_CONFIG_FILE);
    }


    /**
     * Constructor.
     */
    public VismoEventRegistry() {
        this(new ZMQSockets(new ZContext()), "tcp://127.0.0.1:" + conf.getConsumersPort());
    }


    /**
     * Constructor.
     * 
     * @param zmq
     * @param address
     */
    public VismoEventRegistry(final ZMQSockets zmq, final String address) {
        super(zmq, address, false);
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
