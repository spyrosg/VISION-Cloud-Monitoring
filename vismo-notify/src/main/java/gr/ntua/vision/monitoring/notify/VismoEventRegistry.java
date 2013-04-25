package gr.ntua.vision.monitoring.notify;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.IOException;

import org.zeromq.ZContext;


/**
 * The default event registry. For details, see {@link EventRegistry}. Exactly one instance should exist in each event consumer
 * application.
 */
public class VismoEventRegistry extends EventRegistry {
    /**
     * Default constructor. This is used to register and receive events from the cluster head.
     */
    public VismoEventRegistry() {
        this(getDefaultRegistryAddress(loadConfigFrom(VismoConfiguration.VISMO_CONFIG_FILE)));
    }


    /**
     * Constructor.
     * 
     * @param registryAddress
     *            The address and port of the machine to register and receive events from. The address should be of the form:
     *            <code>"tcp://" + numeric-ip + ":" + port-no</code>.
     */
    public VismoEventRegistry(final String registryAddress) {
        this(new ZMQFactory(new ZContext()), registryAddress);
    }


    /**
     * Constructor.
     * 
     * @param socketFactory
     *            the socket factory.
     * @param address
     *            the address to connect for incoming events.
     */
    public VismoEventRegistry(final ZMQFactory socketFactory, final String address) {
        super(socketFactory, address);
    }


    /**
     * @param conf
     *            the configuration object.
     * @return the default registry address.
     */
    private static String getDefaultRegistryAddress(final VismoConfiguration conf) {
        return "tcp://" + conf.getClusterHead() + ":" + conf.getClusterHeadPort();
    }


    /**
     * Load the configuration.
     * 
     * @param configFile
     * @return the configuration object.
     */
    private static VismoConfiguration loadConfigFrom(final String configFile) {
        try {
            return new VismoConfiguration(configFile);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
