package gr.ntua.vision.monitoring;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * A plain configuration object, backed by a {@link Properties} object.
 */
public abstract class PropertiesConfiguration {
    /** the properties object that backs the configuration. */
    private final Properties props;


    /**
     * Constructor.
     * 
     * @param props
     *            the properties object.
     */
    public PropertiesConfiguration(final Properties props) {
        this.props = props;
    }


    /**
     * Constructor.
     * 
     * @param filename
     *            the file to load the properties from.
     * @throws IOException
     */
    public PropertiesConfiguration(final String filename) throws IOException {
        this.props = new Properties();
        loadFromFile(props, filename);
    }


    /**
     * Get the value for the given property name.
     * 
     * @param name
     *            the property name.
     * @return the value of the property name.
     */
    protected String get(final String name) {
        return props.getProperty(name);
    }


    /**
     * Update the value for the given property name.
     * 
     * @param name
     *            the property name.
     * @param value
     *            the property value.
     */
    protected void put(final String name, final String value) {
        props.setProperty(name, value);
    }


    /**
     * Load the properties from the file.
     * 
     * @param props
     *            the properties object.
     * @param filename
     *            the file to load.
     * @throws IOException
     */
    private static void loadFromFile(final Properties props, final String filename) throws IOException {
        final InputStream inp = new BufferedInputStream(new FileInputStream(filename));

        try {
            props.load(inp);
        } finally {
            inp.close();
        }
    }
}
