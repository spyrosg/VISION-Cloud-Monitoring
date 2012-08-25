package endtoend;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.EventDistributor;
import gr.ntua.vision.monitoring.LocalEventsCollector;
import gr.ntua.vision.monitoring.LocalEventsCollectorFactory;
import gr.ntua.vision.monitoring.Vismo;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoFactory;
import gr.ntua.vision.monitoring.udp.UDPClient;
import gr.ntua.vision.monitoring.udp.UDPFactory;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.zeromq.ZContext;


/**
 * This object is used to drive/direct the execution of the top level monitoring objects.
 */
public class MonitoringDriver {
    /***/
    private final VismoConfiguration   conf;
    /***/
    private final EventCounterListener counter = new EventCounterListener(10);
    /***/
    private Vismo                      vismo   = null;


    /**
     * Constructor. Prepare to run the monitoring application.
     * 
     * @param conf
     */
    public MonitoringDriver(final VismoConfiguration conf) {
        this.conf = conf;
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
        final UDPClient client = new UDPFactory(udpPort).buildClient();
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
     * @throws SocketException
     */
    public void setup() throws SocketException {
        final ZContext ctx = new ZContext();
        final LocalEventsCollector receiver = new LocalEventsCollectorFactory(conf, ctx).build();

        receiver.subscribe(new EventDistributor(ctx, conf.getConsumersPoint()));
        receiver.subscribe(counter);

        vismo = new VismoFactory(conf).build();
        vismo.addTask(receiver);
    }


    /**
     * Stop the application, causing it to leave the cluster.
     */
    public void shutdown() {
        counter.haveReceivedEnoughMessages();

        if (vismo != null)
            vismo.stop();
    }


    /**
     * Start the application. The application should join the cluster and start sending new events.
     */
    public void start() {
        if (vismo != null)
            vismo.start();
    }
}
