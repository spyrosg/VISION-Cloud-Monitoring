package gr.ntua.vision.monitoring;

import java.net.SocketException;


/**
 * 
 */
public class VismoClusterPolicy {
    /***/
    private final VismoConfiguration conf;
    /***/
    private final VMInfo             vminfo;


    /**
     * Constructor.
     * 
     * @param conf
     * @param vminfo
     */
    public VismoClusterPolicy(final VismoConfiguration conf, final VMInfo vminfo) {
        this.conf = conf;
        this.vminfo = vminfo;
    }


    /**
     * Return the appropriate element according to our position in the cluster.
     * 
     * @return
     * @throws SocketException
     */
    public VismoCloudElement createElementForCluster() throws SocketException {
        return new OldVismoNode(vminfo);
    }
}
