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
    private static final String CLUSTER_HEAD_PORT_PROPERTY = "workers.head.port";
    /***/
    private static final String CLUSTER_HEAD_PROPERTY      = "testClusterHead";
    /***/
    private static final String CLUSTER_MACHINES_PROPERTY  = "testClusterMachines";
    /***/
    private static final String CLUSTER_NAME_PROPERTY      = "testClusterName";
    /***/
    private static final String CONSUMERS_PORT_PROPERTY    = "consumers.port";
    /***/
    private static final String PRODUCERS_PORT_PROPERTY    = "producers.port";
    /***/
    private static final String UDP_PORT_PROPERTY          = "udp.port";


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
     * @return the cluster head's ip.
     */
    public String getClusterHead() {
        return get(CLUSTER_HEAD_PROPERTY);
    }


    /**
     * @return the cluster head's port.
     */
    public int getClusterHeadPort() {
        return Integer.valueOf(get(CLUSTER_HEAD_PORT_PROPERTY));
    }


    /**
     * @return the consumers port.
     */
    public int getConsumersPort() {
        return Integer.valueOf(get(CONSUMERS_PORT_PROPERTY));
    }


    /**
     * @return the producers port.
     */
    public int getProducersPort() {
        return Integer.valueOf(get(PRODUCERS_PORT_PROPERTY));
    }


    /**
     * @return the list of the ips of machines running in the cluster.
     */
    public List<String> getTestClusterMachines() {
        final String value = get(CLUSTER_MACHINES_PROPERTY);

        return Arrays.asList(value.split(", "));
    }


    /**
     * @return the name of the cluster we're running in.
     */
    public String getTestClusterName() {
        return get(CLUSTER_NAME_PROPERTY);
    }


    /**
     * @return the udp server's port.
     */
    public int getUDPPort() {
        return Integer.valueOf(get(UDP_PORT_PROPERTY));
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
