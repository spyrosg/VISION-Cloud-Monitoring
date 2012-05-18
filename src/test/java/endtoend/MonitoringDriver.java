package endtoend;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.LogEventListener;
import gr.ntua.vision.monitoring.MonitoringInstance;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventListener;
import gr.ntua.vision.monitoring.events.EventReceiver;
import gr.ntua.vision.monitoring.udp.UDPClient;
import gr.ntua.vision.monitoring.udp.UDPServer;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.zeromq.ZContext;


/**
 * This object is used to drive/direct
 */
public class MonitoringDriver {
    /**
     *
     */
    private static class EventCounterListener implements EventListener {
        /***/
        private final int noExpectedEvents;
        /***/
        private int       noReceivedEvents = 0;


        /**
         * @param noExpectedEvents
         */
        public EventCounterListener(final int noExpectedEvents) {
            this.noExpectedEvents = noExpectedEvents;
        }


        /**
         * 
         */
        public void haveReceivedEnoughMessages() {
            assertTrue("not enough events received: " + noReceivedEvents, noReceivedEvents >= noExpectedEvents);
        }


        /**
         * @see gr.ntua.vision.monitoring.events.EventListener#notify(gr.ntua.vision.monitoring.events.Event)
         */
        @Override
        public void notify(final Event e) {
            ++noReceivedEvents;
        }
    }

    /***/
    private final EventCounterListener counter = new EventCounterListener(10);
    /***/
    private final MonitoringInstance   inst;


    /**
     * Constructor. Prepare to run the monitoring application.
     */
    public MonitoringDriver() {
        this.inst = new MonitoringInstance();
    }


    /**
     * @param ctx
     * @param eventsEndPoint
     */
    public void addEventReceiver(final ZContext ctx, final String eventsEndPoint) {
        final EventReceiver receiver = new EventReceiver(ctx, eventsEndPoint);

        receiver.subscribe(new LogEventListener());
        receiver.subscribe(counter);
        // receiver.subscribe(new EventDistributor());
        inst.addTask(receiver);
    }


    /**
     * @param udpPort
     * @throws SocketException
     */
    public void addUDPServer(final int udpPort) throws SocketException {
        inst.addTask(new UDPServer(udpPort, inst));
    }


    /**
     * Use the udp client to get the status of the monitoring instance. If the instance is normally running, it should return the
     * pid of the running jvm.
     * 
     * @param udpPort
     * @throws IOException
     */
    public void reportsMonitoringStatus(final int udpPort) throws IOException {
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
        counter.haveReceivedEnoughMessages();
        inst.stop();
    }


    /**
     * Start the application. The application should join the cluster and start sending new events.
     * 
     * @throws SocketException
     */
    public void start() throws SocketException {
        inst.start();
    }
}
