package gr.ntua.vision.monitoring.notify;

import gr.ntua.vision.monitoring.VismoConfiguration;

import java.io.IOException;


/**
 *
 */
public class VismoEventRegistry extends EventRegistry {
    /***/
    private static VismoConfiguration conf;
    /***/
    private static final String       VISMO_CONFIG_FILE = "/etc/visioncloud_vismo.conf";

    static {
        loadConfiguration();
    }


    /**
     * Constructor.
     */
    public VismoEventRegistry() {
        super("tcp://127.0.0.1:" + conf.getConsumersPort());
    }


    /**
     * Constructor.
     * 
     * @param debug
     *            when <code>true</code>, it activates the console logger for this package.
     */
    public VismoEventRegistry(final boolean debug) {
        super("tcp://127.0.0.1:" + conf.getConsumersPort(), debug);
    }


    /**
     * Try to load the configuration. First try reading the file specified in the system property; if the property is null, try
     * loading the configuration from inside the jar.
     */
    private static void loadConfiguration() {
        try {
            conf = new VismoConfiguration(VISMO_CONFIG_FILE);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
