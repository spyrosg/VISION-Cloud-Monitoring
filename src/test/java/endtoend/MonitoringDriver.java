package endtoend;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.MonitoringInstance;
import gr.ntua.vision.monitoring.UDPClient;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;


/**
 *
 */
public class MonitoringDriver {
    /***/
    private final MonitoringInstance inst;
    /***/
    private final int                udpPort;


    /**
     * Constructor. Prepare to run the monitoring application.
     * 
     * @param udpPort
     */
    public MonitoringDriver(final int udpPort) {
        this.udpPort = udpPort;
        this.inst = new MonitoringInstance();
    }


    /**
     * Use the udp client to get the status of the monitoring instance. If the instance is normally running, it should return the
     * pid of the running jvm.
     * 
     * @throws IOException
     */
    public void reportsStatus() throws IOException {
        final UDPClient client = new UDPClient(udpPort);
        String resp = null;

        for (int i = 0; i < 3; ++i)
            try {
                resp = client.getServiceStatus();
                break;
            } catch (final SocketTimeoutException e) {
                //
            }

        assertNotNull(resp);
        final int pid = Integer.parseInt(resp);

        assertTrue(pid > 1);
    }


    /**
     * Stop the application, causing it to leave the cluster.
     */
    public void shutdown() {
        inst.stop();
    }


    /**
     * Start the application. The application should join the cluster and start sending new events.
     * 
     * @throws SocketException
     */
    public void start() throws SocketException {
        inst.start(udpPort);
    }
}
