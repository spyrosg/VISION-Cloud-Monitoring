package gr.ntua.vision.monitoring;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


/**
 * A plain configuration object, backed by a {@link Properties} object.
 */
public abstract class PropertiesConfiguration {
    /***/
    private static final String SEP = ", ";
    /** the properties object that backs the configuration. */
    private final Properties    props;


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
        final String v = props.getProperty(name);

        requireNonNullValue(v, name);

        return v;
    }


    /**
     * Get the value for the given property name as an integer.
     * 
     * @param name
     *            the property name.
     * @return the value of the property name.
     */
    protected int getAsInt(final String name) {
        return Integer.valueOf(get(name));
    }


    /**
     * Get the value for the given property name as a list of strings.
     * 
     * @param name
     *            the property name.
     * @return the value of the property name.
     */
    protected List<String> getAsList(final String name) {
        return Arrays.asList(get(name).split(SEP));
    }


    /**
     * Get the value for the given property name as a long.
     * 
     * @param name
     *            the property name.
     * @return the value of the property name.
     */
    protected long getAsLong(final String name) {
        return Long.valueOf(get(name));
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
     * Load the properties from the stream.
     * 
     * @param props
     *            the properties object.
     * @param inp
     *            the stream to load from.
     * @throws IOException
     */
    protected static void loadFromStream(final Properties props, final InputStream inp) throws IOException {
        try {
            props.load(inp);
        } finally {
            inp.close();
        }
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
        loadFromStream(props, new BufferedInputStream(new FileInputStream(filename)));
    }


    /**
     * @param val
     * @param name
     */
    private static void requireNonNullValue(final String val, final String name) {
        if (val == null)
            throw new Error("undefined configuration property: '" + name + "'");
    }
}
