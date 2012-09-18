package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.zmq.VismoSocket;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;


/**
 * This object is responsible for setting up the cluster. More accurately, it knows how to setup either a simple node or a cluster
 * leader node.
 */
public class ClusterController {
    /***/
    private static final Logger            log = LoggerFactory.getLogger(ClusterController.class);
    // TODO: obviously remove this. Find a better, more oo way to implement this responsibilities.
    /** the configuration object. */
    private final VismoConfiguration       conf;
    /** the factory. */
    private final VismoCloudElementFactory factory;
    /** the vminfo object. */
    private final VMInfo                   vminfo;
    /** the zmq object. */
    private final ZMQSockets               zmq = new ZMQSockets(new ZContext());


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
        this.factory = new VismoCloudElementFactory(vminfo);
    }


    /**
     * Setup and return the proper cloud element.
     * 
     * @return either a {@link VismoWorkerNode} or a {@link VismoClusterHead} instance.
     * @throws SocketException
     */
    public VismoCloudElement setup() throws SocketException {
        logConfig();

        final EventSource local = getLocalHostEventSource();

        if (hostIsClusterHead())
            return setupVismoClusterHeadNode(local);

        return setupVismoWorkerNode(local);
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
    private EventSource getLocalHostEventSource() {
        final VismoSocket sock = zmq.newBoundPullSocket(conf.getProducersPoint());

        return new BasicEventSource(new VismoEventFactory(), sock);
    }


    /**
     * Check whether localhost is the cluster head (according to the configuration).
     * 
     * @return <code>true</code> when localhost is the cluster head, <code>false</code> otherwise.
     * @throws SocketException
     */
    private boolean hostIsClusterHead() throws SocketException {
        return getClusterHeadIP().equals(vminfo.getAddress().getHostAddress());
    }


    /**
     * 
     */
    private void logConfig() {
        log.debug("*** cluster configuration");
        log.debug("cluster name is '{}'", conf.getTestClusterName());
        log.debug("cluster machine's ips: {}", conf.getTestClusterMachines());
        log.debug("cluster head is at {}", getClusterHeadIP());
    }


    /**
     * Setup and return the cluster head instance.
     * 
     * @param local
     *            the local events source.
     * @return a {@link VismoClusterHead} object.
     */
    private VismoCloudElement setupVismoClusterHeadNode(final EventSource local) {
        final VismoSocket sock = zmq.newBoundPubSocket(conf.getConsumersPoint());
        final BasicEventSink sink = new BasicEventSink(sock);
        final VismoSocket other = zmq.newBoundPullSocket(toZSocket("localhost", conf.getNodeHeadPort()));
        final BasicEventSource source = new BasicEventSource(new VismoEventFactory(), other);

        return factory.createVismoClusterHeadNode(sink, Arrays.asList(local, source));
    }


    /**
     * Setup and return the cluster worker instance.
     * 
     * @param local
     *            the local events source.
     * @return a {@link VismoWorkerNode} object.
     * @throws SocketException
     */
    private VismoCloudElement setupVismoWorkerNode(final EventSource local) throws SocketException {
        final VismoSocket sock = zmq.newConnectedPushSocket(workerToHeadSocket());
        final BasicEventSink sink = new BasicEventSink(sock);

        return factory.createVismoWorkerNode(local, sink);
    }


    /**
     * @return the full ip address that is used by the cluster workers to talk to the head.
     */
    private String workerToHeadSocket() {
        return toZSocket(getClusterHeadIP(), conf.getNodeHeadPort());
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
    private static String toZSocket(final String ip, final String port) {
        return "tcp://" + ip + ":" + port;
    }
}
