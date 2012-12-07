package gr.ntua.vision.monitoring.policy;

import gr.ntua.vision.monitoring.CloudHeadNodeFactory;
import gr.ntua.vision.monitoring.ClusterHeadNodeFactory;
import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoService;
import gr.ntua.vision.monitoring.WorkerNodeFactory;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;


/**
 * Provides a {@link VismoService} using the configuration.
 */
public class StaticConfigPolicy implements NodePolicy {
    /** the log target. */
    private static final Logger      log = LoggerFactory.getLogger(StaticConfigPolicy.class);
    /** the configuration object. */
    private final VismoConfiguration conf;


    /**
     * Constructor.
     * 
     * @param conf
     *            the configuration object.
     */
    public StaticConfigPolicy(final VismoConfiguration conf) {
        this.conf = conf;
    }


    /**
     * @see gr.ntua.vision.monitoring.policy.NodePolicy#build(gr.ntua.vision.monitoring.VMInfo)
     */
    @Override
    public VismoService build(final VMInfo vminfo) {
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
     */
    private boolean hostIsCloudHead(final VMInfo vminfo) {
        return conf.isIPCloudHead(vminfo.getAddress().getHostAddress());
    }


    /**
     * Check whether localhost is a cluster head (according to the configuration).
     * 
     * @param vminfo
     * @return <code>true</code> when localhost is a cluster head, <code>false</code> otherwise.
     */
    private boolean hostIsClusterHead(final VMInfo vminfo) {
        return conf.isIPClusterHead(vminfo.getAddress().getHostAddress());
    }


    /**
     * @param vminfo
     */
    private void logConfig(final VMInfo vminfo) {
        log.debug("is cluster head? {}", hostIsClusterHead(vminfo));
        log.debug("is cloud head? {}", hostIsCloudHead(vminfo));
    }
}
