package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.zmq.VismoSocket;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;
import java.util.ArrayList;
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
    /***/
    private final VismoConfiguration       conf;
    private final VismoCloudElementFactory factory;
    /***/
    private final VMInfo                   vminfo;
    /***/
    private final ZMQSockets               zmq = new ZMQSockets(new ZContext());


    /**
     * Constructor.
     * 
     * @param vminfo
     * @param conf
     */
    public ClusterController(final VMInfo vminfo, final VismoConfiguration conf) {
        this.vminfo = vminfo;
        this.conf = conf;
        this.factory = new VismoCloudElementFactory(vminfo);
        logSetup();
    }


    /**
     * @return
     * @throws SocketException
     */
    public VismoCloudElement setup() throws SocketException {
        final BasicEventSource local = new BasicEventSource(new VismoEventFactory(),
                getLocalProducerSocket(conf.getProducersPoint()));

        if (hostIsClusterHead())
            return setupVismoClusterHeadNode(local);

        return setupVismoWorkerNode(local);
    }


    /**
     * @param address
     * @return
     */
    private VismoSocket getClusterConsumersSocket(final String address) {
        return zmq.newBoundPubSocket(address);
    }


    /**
     * @return
     */
    private String getClusterHeadIP() {
        final List<String> machines = conf.getTestClusterMachines();

        if (machines.size() != 3)
            throw new Error("assuming cluster with 3 nodes");

        return selectClusterHead(machines);
    }


    /**
     * @return
     */
    private String getClusterHeadPoint() {
        return "tcp://" + getClusterHeadIP() + ":" + conf.getNodeHeadPort();
    }


    /**
     * @param address
     * @return
     */
    private VismoSocket getClusterHeadSocket(final String address) {
        return zmq.newConnectedPushSocket(address);
    }


    /**
     * @param address
     * @return
     */
    private VismoSocket getLocalProducerSocket(final String address) {
        return zmq.newBoundPullSocket(address);
    }


    /**
     * @return
     */
    private ArrayList<EventSource> getNodePoints() {
        final ArrayList<EventSource> sources = new ArrayList<EventSource>();

        for (final String ip : conf.getTestClusterMachines())
            if (!ip.equals(getClusterHeadIP())) {
                // FIXME:
                final BasicEventSource source = new BasicEventSource(new VismoEventFactory(), null);

                sources.add(source);
            }

        return sources;
    }


    /**
     * @return
     */
    private boolean hostIsClusterHead() {
        // return getClusterHeadIP().equals(vminfo.getAddress().getHostAddress());
        return true;
    }


    /**
     * 
     */
    private void logSetup() {
        log.debug("*** showing cluster configuration");
        log.debug("cluster name is '{}'", conf.getTestClusterName());
        log.debug("cluster machine's ips: {}", conf.getTestClusterMachines());
        log.debug("cluster head is {}", getClusterHeadIP());
    }


    /**
     * @param localHostSource
     * @return
     */
    private VismoCloudElement setupVismoClusterHeadNode(final BasicEventSource localHostSource) {
        final BasicEventSink sink = new BasicEventSink(getClusterConsumersSocket(conf.getConsumersPoint()));
        final ArrayList<EventSource> otherNodes = getNodePoints();

        otherNodes.add(localHostSource);

        return factory.createVismoClusterHeadNode(sink, otherNodes);
    }


    /**
     * @param localHostSource
     * @return
     * @throws SocketException
     */
    private VismoCloudElement setupVismoWorkerNode(final BasicEventSource localHostSource) throws SocketException {
        final BasicEventSink sink = new BasicEventSink(getClusterHeadSocket(getClusterHeadPoint()));

        return factory.createVismoWorkerNode(localHostSource, sink);
    }


    /**
     * @param machines
     * @return
     */
    private static String selectClusterHead(final List<String> machines) {
        return machines.get(machines.size() / 2);
    }
}
