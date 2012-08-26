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
    private static final String       VISMO_CONFIG_RESOURCE        = "/config.properties";
    /***/
    private static final String       VISMO_CONFIG_SYSTEM_PROPERTY = "vismo.config.properties";

    static {
        try {
            loadConfiguration();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Constructor.
     */
    public VismoEventRegistry() {
        super(conf.getConsumersPoint());
    }


    /**
     * Constructor.
     * 
     * @param debug
     *            when <code>true</code>, it activates the console logger for this package.
     */
    public VismoEventRegistry(final boolean debug) {
        super(conf.getConsumersPoint(), debug);
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

        conf = VismoConfiguration.loadFromResource(VismoEventRegistry.class.getResourceAsStream((VISMO_CONFIG_RESOURCE)));
    }
}
