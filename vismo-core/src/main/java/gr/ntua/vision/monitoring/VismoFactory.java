package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.scheduling.JVMStatusReportTask;
import gr.ntua.vision.monitoring.sinks.BasicEventSink;
import gr.ntua.vision.monitoring.sources.BasicEventSource;
import gr.ntua.vision.monitoring.udp.UDPFactory;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;


/**
 * 
 */
public class VismoFactory {
    /***/
    private static final Logger      log        = LoggerFactory.getLogger(VismoFactory.class);
    /***/
    private static final long        ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);
    /***/
    private final VismoConfiguration conf;


    /**
     * Constructor.
     * 
     * @param conf
     */
    public VismoFactory(final VismoConfiguration conf) {
        this.conf = conf;
    }


    /**
     * Setup the service.
     * 
     * @return the service.
     * @throws SocketException
     */
    public VismoService build() throws SocketException {
        logConfig();

        final VismoVMInfo vminfo = new VismoVMInfo();
        final VismoService service = new VismoService(vminfo);
        final ZMQSockets zmq = new ZMQSockets(new ZContext());

        if (hostIsClusterHead(vminfo.getAddress().getHostAddress())) {

        } else {
            final BasicEventSource localSource = getLocalSource(zmq);
            final BasicEventSink clusterHead = new BasicEventSink(zmq.newConnectedPushSocket("tcp://" + conf.getClusterHead()
                    + ":" + conf.getClusterHeadPort()));

            localSource.subscribe(new PassThroughChannel(clusterHead));
            service.addTask(localSource);
        }

        service.addTask(new UDPFactory(conf.getUDPPort()).buildServer(service));
        service.addTask(new JVMStatusReportTask(ONE_MINUTE));

        return service;
    }


    /**
     * @return
     */
    private BasicEventSource getLocalSource(final ZMQSockets zmq) {
        return new BasicEventSource(new VismoEventFactory(), zmq.newBoundPullSocket(conf.getProducersPoint()));
    }


    /**
     * Check whether localhost is the cluster head (according to the configuration).
     * 
     * @param hostIP
     * @return <code>true</code> when localhost is the cluster head, <code>false</code> otherwise.
     */
    private boolean hostIsClusterHead(final String hostIP) {
        return conf.getClusterHead().equals(hostIP);
    }


    /**
     * 
     */
    private void logConfig() {
        log.trace("*** cluster configuration");
        log.trace("*** name is '{}'", conf.getTestClusterName());
        log.trace("*** machines: {}", conf.getTestClusterMachines());
        log.trace("*** head is at {}", conf.getClusterHead());
    }
}
