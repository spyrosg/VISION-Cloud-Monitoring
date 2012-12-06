package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;


/**
 * 
 */
public class VismoServiceFactory {
    /***/
    private static final Logger      log = LoggerFactory.getLogger(VismoServiceFactory.class);
    /***/
    private final VismoConfiguration conf;


    /**
     * Constructor.
     * 
     * @param conf
     */
    public VismoServiceFactory(final VismoConfiguration conf) {
        this.conf = conf;
    }


    /**
     * @param vminfo
     * @return the {@link VismoService}.
     * @throws SocketException
     */
    public VismoService build(final VismoVMInfo vminfo) throws SocketException {
        final ZMQSockets zmq = new ZMQSockets(new ZContext());

        logConfig(vminfo);

        if (hostIsCloudHead(vminfo))
            return new CloudHeadNodeFactory(conf, zmq).build(vminfo);
        else if (hostIsClusterHead(vminfo))
            return new ClusterHeadNodeFactory(conf, zmq).build(vminfo);
        else
            return new WorkerNodeFactory(conf, zmq).build(vminfo);
    }


    /**
     * Check whether localhost is the cluster head (according to the configuration).
     * 
     * @param vminfo
     * @return <code>true</code> when localhost is a cloud head, <code>false</code> otherwise.
     * @throws SocketException
     */
    private boolean hostIsCloudHead(final VMInfo vminfo) throws SocketException {
        return conf.isIPCloudHead(vminfo.getAddress().getHostAddress());
    }


    /**
     * Check whether localhost is a cluster head (according to the configuration).
     * 
     * @param vminfo
     * @return <code>true</code> when localhost is a cluster head, <code>false</code> otherwise.
     * @throws SocketException
     */
    private boolean hostIsClusterHead(final VMInfo vminfo) throws SocketException {
        return conf.isIPClusterHead(vminfo.getAddress().getHostAddress());
    }


    /**
     * @param vminfo
     * @throws SocketException
     */
    private void logConfig(final VMInfo vminfo) throws SocketException {
        log.debug("is cluster head? {}", hostIsClusterHead(vminfo));
        log.debug("is cloud head? {}", hostIsCloudHead(vminfo));
    }
}
