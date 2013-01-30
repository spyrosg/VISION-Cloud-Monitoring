package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.util.List;
import java.util.Properties;


/**
 * 
 */
public class VismoConfiguration extends PropertiesConfiguration {
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
    private static final String MON_GROUP_ADDR_PROPERTY    = "mon.group.addr";
    /***/
    private static final String MON_GROUP_PORT_PROPERTY    = "mon.group.port";
    /***/
    private static final String MON_PING_PERIOD_PROPERTY   = "mon.ping.period";
    /***/
    private static final String PRODUCERS_POINT_PROPERTY   = "producers.point";
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
     * @return the monitoring multicast group address.
     */
    public String getMonGroupAddr() {
        return get(MON_GROUP_ADDR_PROPERTY);
    }


    /**
     * @return the monitoring multicast group port.
     */
    public int getMonGroupPort() {
        return getAsInt(MON_GROUP_PORT_PROPERTY);
    }


    /**
     * @return the multicast group ping period, in millies.
     */
    public long getMonPingPeriod() {
        return getAsLong(MON_PING_PERIOD_PROPERTY);
    }


    /**
     * @return the producers address.
     */
    public String getProducersPoint() {
        return get(PRODUCERS_POINT_PROPERTY);
    }


    /**
     * @return the udp server's port.
     */
    public int getUDPPort() {
        return getAsInt(UDP_PORT_PROPERTY);
    }


    /**
     * Check that given ip one of the cloud heads.
     * 
     * @param ip
     *            the ip.
     * @return <code>true</code> iff the given ip is one of the cloud head ips.
     */
    public boolean isIPCloudHead(final String ip) {
        return getCloudHeads().contains(ip);
    }


    /**
     * Check that given ip is the cluster head.
     * 
     * @param ip
     *            the ip.
     * @return <code>true</code> iff the given ip and the ip of cluster head are the same.
     */
    public boolean isIPClusterHead(final String ip) {
        return getClusterHead().equals(ip);
    }
}
