package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


/**
 * 
 */
public class VismoConfiguration extends PropertiesConfiguration {
    /***/
    private static final String CONSUMERS_POINT_PROPERTY       = "consumers.point";
    /***/
    private static final String PRODUCERS_POINT_PROPERTY       = "producers.point";
    /***/
    private static final String UDP_PORT_PROPERTY              = "udp.port";
    /***/
    private static final String TEST_CLUSTER_NAME_PROPERTY     = "testClusterName";
    /***/
    private static final String TEST_CLUSTER_MACHINES_PROPERTY = "testClusterMachines";


    /**
     * Constructor.
     * 
     * @param props
     *            the properties object.
     */
    public VismoConfiguration(final Properties props) {
        super(props);
    }


    /**
     * Constructor.
     * 
     * @param filename
     *            the file to load the properties from.
     * @throws IOException
     */
    public VismoConfiguration(final String filename) throws IOException {
        super(filename);
    }


    /**
     * @return the consumers port.
     */
    public String getConsumersPoint() {
        return get(CONSUMERS_POINT_PROPERTY);
    }


    /**
     * @return the producers port.
     */
    public String getProducersPoint() {
        return get(PRODUCERS_POINT_PROPERTY);
    }


    /**
     * @return the udp server port.
     */
    public int getUDPPort() {
        return Integer.valueOf(get(UDP_PORT_PROPERTY));
    }


    /**
     * @return the name of the cluster we're running in.
     */
    public String getTestClusterName() {
        return get(TEST_CLUSTER_NAME_PROPERTY);
    }


    /**
     * @return the list of the ips of machines running in the cluster.
     */
    public List<String> getTestClusterMachines() {
        final String value = get(TEST_CLUSTER_MACHINES_PROPERTY);

        return Arrays.asList(value.split(", "));
    }


    /**
     * Load configuration from the given stream.
     * 
     * @param inp
     *            the input stream.
     * @return a {@link VismoConfiguration} object.
     * @throws IOException
     */
    public static VismoConfiguration loadFromResource(final InputStream inp) throws IOException {
        final Properties props = new Properties();

        loadFromStream(props, inp);

        return new VismoConfiguration(props);
    }
}
