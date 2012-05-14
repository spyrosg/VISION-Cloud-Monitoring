package endtoend;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.Event;
import gr.ntua.vision.monitoring.EventListener;
import gr.ntua.vision.monitoring.EventReceiver;
import gr.ntua.vision.monitoring.LogEventListener;
import gr.ntua.vision.monitoring.MonitoringInstance;
import gr.ntua.vision.monitoring.UDPClient;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.zeromq.ZContext;


/**
 *
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
         * @see gr.ntua.vision.monitoring.EventListener#notify(gr.ntua.vision.monitoring.Event)
         */
        @Override
        public void notify(final Event e) {
            ++noReceivedEvents;
        }
    }

    /***/
    private final EventCounterListener counter     = new EventCounterListener(10);
    /***/
    private final MonitoringInstance   inst;
    /***/
    private final LogEventListener     logListener = new LogEventListener();
    /***/
    private final EventReceiver        receiver;
    /***/
    private final int                  udpPort;


    /**
     * Constructor. Prepare to run the monitoring application.
     * 
     * @param ctx
     * @param udpPort
     * @param eventsEndPoint
     */
    public MonitoringDriver(final ZContext ctx, final int udpPort, final String eventsEndPoint) {
        this.udpPort = udpPort;
        this.inst = new MonitoringInstance();
        this.receiver = new EventReceiver(ctx, eventsEndPoint);
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
        counter.haveReceivedEnoughMessages();
        inst.stop();
    }


    /**
     * Start the application. The application should join the cluster and start sending new events.
     * 
     * @throws SocketException
     */
    public void start() throws SocketException {
        inst.start(udpPort);
        receiver.add(logListener);
        receiver.add(counter);
        receiver.start();
    }
}
