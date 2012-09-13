package gr.ntua.vision.monitoring;

import java.net.SocketException;


/**
 * 
 */
public class VismoElementFactory {
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
    public VismoElementFactory(final VismoConfiguration conf, final VMInfo vminfo) {
        this.conf = conf;
        this.vminfo = vminfo;
    }


    /**
     * Return the appropriate element according to our position in the cluster.
     * 
     * @return
     * @throws SocketException
     */
    public VismoElement createElementForCluster() throws SocketException {
        return new VismoNode(vminfo);
    }
}
