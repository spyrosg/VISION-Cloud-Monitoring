package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.sinks.BasicEventSink;
import gr.ntua.vision.monitoring.sinks.PubSubEventSink;
import gr.ntua.vision.monitoring.sources.BasicEventSource;
import gr.ntua.vision.monitoring.zmq.VismoSocket;

import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//TODO: obviously remove this. Find a better, more oo way to implement this responsibilities.

/**
 * This object is responsible for setting up the cluster. More accurately, it knows how to setup either a simple node or a cluster
 * leader node.
 */
public class ClusterController {
    /***/
    private static final Logger      log = LoggerFactory.getLogger(ClusterController.class);
    /** the configuration object. */
    private final VismoConfiguration conf;
    /** the vminfo object. */
    private final VMInfo             vminfo;


    /**
     * Constructor.
     * 
     * @param vminfo
     *            the vminfo object.
     * @param conf
     *            the configuration object.
     */
    public ClusterController(final VMInfo vminfo, final VismoConfiguration conf) {
        this.vminfo = vminfo;
        this.conf = conf;
    }


    /**
     * Setup and return the proper cloud element.
     * 
     * @param service
     * @return either a {@link VismoWorkerNode} or a {@link VismoClusterHead} instance.
     * @throws SocketException
     */
    public VismoCloudElement selectElement(final VismoService service) throws SocketException {
        logConfig();

        if (hostIsClusterHead())
            return new VismoClusterHead(service);

        return new VismoWorkerNode(service);
    }


    /**
     * @return the ip of the machine running the cluster head.
     */
    private String getClusterHeadIP() {
        final List<String> machines = conf.getTestClusterMachines();

        if (machines.size() != 3)
            throw new Error("assuming cluster with 3 nodes");

        return selectClusterHead(machines);
    }


    /**
     * @return the event source for local host events.
     */
    private BasicEventSource getLocalHostEventSource() {
        final VismoSocket sock = zmq.newBoundPullSocket(conf.getProducersPort());

        return new BasicEventSource(new VismoEventFactory(), sock);
    }


    /**
     * Check whether localhost is the cluster head (according to the configuration).
     * 
     * @return <code>true</code> when localhost is the cluster head, <code>false</code> otherwise.
     * @throws SocketException
     */
    private boolean hostIsClusterHead() throws SocketException {
        // return getClusterHeadIP().equals(vminfo.getAddress().getHostAddress());
        return true;
    }


    /**
     * 
     */
    private void logConfig() {
        log.trace("*** cluster configuration");
        log.trace("cluster name is '{}'", conf.getTestClusterName());
        log.trace("cluster machine's ips: {}", conf.getTestClusterMachines());
        log.trace("cluster head is at {}", getClusterHeadIP());
    }


    /**
     * Setup and return the cluster head instance.
     * 
     * @param local
     *            the local events source.
     * @return a {@link VismoClusterHead} object.
     * @throws SocketException
     */
    private VismoCloudElement setupVismoClusterHeadNode(final BasicEventSource local) throws SocketException {
        final VismoSocket sock = zmq.newBoundPubSocket(conf.getConsumersPort());
        final BasicEventSink sink = new PubSubEventSink(sock);
        final VismoSocket other = zmq.newBoundPullSocket(toZSocket("*", conf.getClusterHeadPort()));
        final BasicEventSource source = new BasicEventSource(new VismoEventFactory(), other);

        return factory.createVismoClusterHeadNode(sink, Arrays.asList(local, source));
    }


    /**
     * new Setup and return the cluster worker instance.
     * 
     * @param local
     *            the local events source.
     * @return a {@link VismoWorkerNode} object.
     * @throws SocketException
     */
    private VismoCloudElement setupVismoWorkerNode(final BasicEventSource local) throws SocketException {
        final VismoSocket sock = zmq.newConnectedPushSocket(workerToHeadSocket());
        final BasicEventSink sink = new BasicEventSink(sock);

        return factory.createVismoWorkerNode(local, sink);
    }


    /**
     * @return the full ip address that is used by the cluster workers to talk to the head.
     */
    private String workerToHeadSocket() {
        return toZSocket(getClusterHeadIP(), conf.getClusterHeadPort());
    }


    /**
     * Return the ip of the machine that is the head of the cluster.
     * 
     * @param machines
     *            the list of machines in the cluster.
     * @return the ip of the machine that is the head of the cluster.
     */
    private static String selectClusterHead(final List<String> machines) {
        return machines.get(machines.size() / 2);
    }


    /**
     * Turn an ip address and port to a zmq device.
     * 
     * @param ip
     *            the ip address.
     * @param port
     *            the ip port.
     * @return the string representation of a zmq device.
     */
    private static String toZSocket(final String ip, final int port) {
        return "tcp://" + ip + ":" + port;
    }
}
