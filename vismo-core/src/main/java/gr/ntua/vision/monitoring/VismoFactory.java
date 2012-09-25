package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.scheduling.JVMStatusReportTask;
import gr.ntua.vision.monitoring.udp.UDPFactory;
import gr.ntua.vision.monitoring.udp.UDPServer;
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
        final UDPServer udpServer = new UDPFactory(conf.getUDPPort()).buildServer(service);
        final VismoCloudElement elem = selectElement(vminfo, service, new ZMQSockets(new ZContext()));

        elem.setup();
        service.addTask(udpServer);
        service.addTask(new JVMStatusReportTask(ONE_MINUTE));

        return service;
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


    /**
     * @param vminfo
     * @param service
     * @return the proper {@link VismoCloudElement}.
     * @throws SocketException
     */
    private VismoCloudElement selectElement(final VMInfo vminfo, final VismoService service, final ZMQSockets zmq)
            throws SocketException {
        return hostIsClusterHead(vminfo.getAddress().getHostAddress()) ? new VismoClusterHead(service, conf, zmq)
                                                                      : new VismoWorkerNode(service, conf, zmq);
    }
}
