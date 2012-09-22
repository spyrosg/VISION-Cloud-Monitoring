package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.scheduling.JVMStatusReportTask;
import gr.ntua.vision.monitoring.udp.UDPFactory;
import gr.ntua.vision.monitoring.udp.UDPServer;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import org.zeromq.ZContext;


/**
 * 
 */
public class VismoFactory {
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
     * @return
     * @throws SocketException
     */
    public VismoService build() throws SocketException {
        final VismoVMInfo vminfo = new VismoVMInfo();
        final VismoService service = new VismoService(vminfo);
        final UDPServer udpServer = new UDPFactory(conf.getUDPPort()).buildServer(service);
        final ClusterController controller = new ClusterController(vminfo, conf);
        final VismoCloudElement elem = controller.selectElement(service);
        final ZMQSockets zmq = new ZMQSockets(new ZContext());

        elem.setup(conf, zmq);
        service.addTask(udpServer);
        service.addTask(new JVMStatusReportTask(ONE_MINUTE));
        elem.start();

        return service;
    }
}
