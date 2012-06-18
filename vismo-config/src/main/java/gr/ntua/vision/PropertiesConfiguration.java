package gr.ntua.vision;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


/**
 *
 */
public class PropertiesConfiguration implements Configuration {
    /** the configuration. */
    private final Properties props = new Properties();


    /**
     * @param inp
     * @throws IOException
     */
    private PropertiesConfiguration(final InputStream inp) throws IOException {
        props.load(inp);
    }


    /**
     * @see gr.ntua.vision.monitoring.Configuration#get(java.lang.String)
     */
    @Override
    public String get(final String key) {
        return props.getProperty(key);
    }


    /**
     * @see gr.ntua.vision.monitoring.Configuration#keys()
     */
    @Override
    public Set<String> keys() {
        final Set<String> keys = Collections.unmodifiableSet(props.stringPropertyNames());
        final Set<String> properKeys = new HashSet<String>();

        for (final String key : keys)
            properKeys.add(key);

        return properKeys;
    }


    /**
     * @see gr.ntua.vision.monitoring.Configuration#put(java.lang.String, java.lang.String)
     */
    @Override
    public void put(final String key, final String val) {
        props.setProperty(key, val);
    }


    /**
     * @param filename
     * @return
     * @throws IOException
     */
    public static PropertiesConfiguration loadFromFile(final String filename) throws IOException {
        final InputStream inp = new BufferedInputStream(new FileInputStream(filename));

        return new PropertiesConfiguration(inp);
    }


    /**
     * @param resource
     * @return
     * @throws IOException
     */
    public static PropertiesConfiguration loadFromResource(final String resource) throws IOException {
        final InputStream inp = PropertiesConfiguration.class.getResourceAsStream(resource);

        return new PropertiesConfiguration(inp);
    }
}
