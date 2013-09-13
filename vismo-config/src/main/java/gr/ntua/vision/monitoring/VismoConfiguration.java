package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.util.List;
import java.util.Properties;


/**
 * 
 */
public class VismoConfiguration extends PropertiesConfiguration {
    /** this is the default path of the configuration. */
    public static final String  VISMO_CONFIG_FILE          = "/etc/visioncloud_vismo.conf";
    /***/
    private static final String CLOUD_HEAD_PORT_PROPERTY   = "cloud.head.port";
    /***/
    private static final String CLOUD_HEADS_PROPERTY       = "cloud.heads";
    /***/
    private static final String CLOUD_NAME_PROPERTY        = "cloud.name";
    /***/
    private static final String CLUSTER_HEAD_PORT_PROPERTY = "cluster.head.port";
    /***/
    private static final String CLUSTER_HEAD_PROPERTY      = "cluster.head";
    /***/
    private static final String CLUSTER_NAME_PROPERTY      = "cluster.name";
    /***/
    private static final String CONSUMERS_PORT_PROPERTY    = "consumers.port";
    /***/
    private static final String PRODUCERS_POINT_PROPERTY   = "producers.point";
    /***/
    private static final String STARTUP_RULES_PROPERY      = "startup.rules";
    /***/
    private static final String UDP_PORT_PROPERTY          = "udp.port";
    /***/
    private static final String WEB_PORT                   = "web.port";


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
     * @return the cloud head's port.
     */
    public int getCloudHeadPort() {
        return getAsInt(CLOUD_HEAD_PORT_PROPERTY);
    }


    /**
     * @return the list of ips of the cloud heads.
     */
    public List<String> getCloudHeads() {
        return getAsList(CLOUD_HEADS_PROPERTY);
    }


    /**
     * @return an identifier of the "cloud".
     */
    public String getCloudName() {
        return get(CLOUD_NAME_PROPERTY);
    }


    /**
     * @return the ip of the cluster head.
     */
    public String getClusterHead() {
        return get(CLUSTER_HEAD_PROPERTY);
    }


    /**
     * @return the cluster head's port.
     */
    public int getClusterHeadPort() {
        return getAsInt(CLUSTER_HEAD_PORT_PROPERTY);
    }


    /**
     * @return an identifier of this cluster.
     */
    public String getClusterName() {
        return get(CLUSTER_NAME_PROPERTY);
    }


    /**
     * @return the consumers port.
     */
    public int getConsumersPort() {
        return getAsInt(CONSUMERS_PORT_PROPERTY);
    }


    /**
     * @return the producers address.
     */
    public String getProducersPoint() {
        return get(PRODUCERS_POINT_PROPERTY);
    }


    /**
     * @return the list of rules to load at startup.
     */
    public List<String> getStartupRules() {
        return getAsList(STARTUP_RULES_PROPERY);
    }


    /**
     * @return the udp server's port.
     */
    public int getUDPPort() {
        return getAsInt(UDP_PORT_PROPERTY);
    }


    /**
     * @return the web server's port.
     */
    public int getWebPort() {
        return getAsInt(WEB_PORT);
    }
}
