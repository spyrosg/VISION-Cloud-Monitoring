package gr.ntua.vision.monitoring.notify;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.IOException;

import org.zeromq.ZContext;


/**
 *
 */
public class VismoEventRegistry extends EventRegistry {
    /**
     * Constructor.
     * 
     * @param vismoConfigFile
     *            the vismo configuration file.
     */
    public VismoEventRegistry(final String vismoConfigFile) {
        this(new ZMQFactory(new ZContext()), toAddr(configWith(vismoConfigFile)));
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
     * Load the configuration.
     * 
     * @param configFile
     * @return the configuration object.
     */
    private static VismoConfiguration configWith(final String configFile) {
        try {
            return new VismoConfiguration(configFile);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param conf
     *            the configuration object.
     * @return the address to connect to.
     */
    private static String toAddr(final VismoConfiguration conf) {
        return "tcp://127.0.0.1:" + conf.getConsumersPort();
    }
}
