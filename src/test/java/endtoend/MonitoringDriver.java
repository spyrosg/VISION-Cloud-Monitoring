package endtoend;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.Main;
import gr.ntua.vision.monitoring.UDPClient;

import java.io.IOException;
import java.net.SocketTimeoutException;


/**
 *
 */
public class MonitoringDriver {
    /***/
    private static final int UDP_SERVER_PORT = 56431;
    /***/
    private final Thread     t;


    /**
     * Constructor. Prepare to run the monitoring application.
     */
    public MonitoringDriver() {
        this.t = new Thread("monitoring-driver") {
            @Override
            public void run() {
                try {
                    Main.main("start");
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        };
        this.t.setDaemon(true);
    }


    /**
     * Stop the application, causing it to leave the cluster.
     */
    public void leaveCluster() {
        try {
            Main.main("stop");
            t.join();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Use the udp client to get the status of the monitoring instance. If the instance is normally running, it should return the
     * pid of the running jvm.
     * 
     * @throws IOException
     */
    public void reportsStatus() throws IOException {
        final UDPClient client = new UDPClient(UDP_SERVER_PORT);
        String resp = null;

        for (int i = 0; i < 3; ++i)
            try {
                resp = client.requestStatus();
                break;
            } catch (SocketTimeoutException e) {
                //
            }

        assertNotNull(resp);
        final int pid = Integer.parseInt(resp);

        assertTrue(pid > 1);
    }


    /**
     * Start the application. The application should join the cluster and start sending new events.
     */
    public void start() {
        t.start();
    }
}
