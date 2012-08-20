package endtoend;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.EventDistributor;
import gr.ntua.vision.monitoring.LocalEventCollector;
import gr.ntua.vision.monitoring.Vismo;
import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.udp.UDPClient;
import gr.ntua.vision.monitoring.udp.UDPServer;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.zeromq.ZContext;


/**
 * This object is used to drive/direct the execution of the top level monitoring objects.
 */
public class MonitoringDriver {
    /***/
    private final EventCounterListener counter = new EventCounterListener(10);
    /***/
    private final Vismo                inst;


    /**
     * Constructor. Prepare to run the monitoring application.
     * 
     * @throws SocketException
     */
    public MonitoringDriver() throws SocketException {
        this.inst = new Vismo(new VismoVMInfo());
    }


    /**
     * Use the udp client to get the status of the monitoring instance. If the instance is normally running, it should return the
     * pid of the running jvm.
     * 
     * @param udpPort
     * @throws IOException
     */
    @SuppressWarnings("static-method")
    public void reportsMonitoringStatus(final int udpPort) throws IOException {
        final UDPClient client = new UDPClient(udpPort);
        String resp = null;

        for (int i = 0; i < 3; ++i)
            try {
                resp = client.getVismoStatus();
                break;
            } catch (final SocketTimeoutException e) {
                //
            }

        assertNotNull(resp);

        final int pid = Integer.parseInt(resp);

        assertTrue(pid > 1);
    }


    /**
     * @param udpPort
     * @param ctx
     * @param localEventsPort
     * @param externalDistributionPort
     * @throws SocketException
     */
    public void setup(final int udpPort, final ZContext ctx, final String localEventsPort, final String externalDistributionPort)
            throws SocketException {
        setupUDPServer(udpPort);
        setupLocalEventCollector(ctx, localEventsPort, externalDistributionPort);
    }


    /**
     * Stop the application, causing it to leave the cluster.
     */
    public void shutdown() {
        counter.haveReceivedEnoughMessages();
        inst.stop();
    }


    /**
     * Start the application. The application should join the cluster and start sending new events.
     */
    public void start() {
        inst.start();
    }


    /**
     * @param ctx
     * @param localEventsPort
     * @param externalDistributionPort
     */
    private void setupLocalEventCollector(final ZContext ctx, final String localEventsPort, final String externalDistributionPort) {
        final LocalEventCollector receiver = new LocalEventCollector(ctx, localEventsPort);
        final EventDistributor distributor = new EventDistributor(ctx, externalDistributionPort);

        receiver.subscribe(counter);
        receiver.subscribe(distributor);
        inst.addTask(receiver);
    }


    /**
     * @param udpPort
     * @throws SocketException
     */
    private void setupUDPServer(final int udpPort) throws SocketException {
        inst.addTask(new UDPServer(udpPort, inst));
    }
}
