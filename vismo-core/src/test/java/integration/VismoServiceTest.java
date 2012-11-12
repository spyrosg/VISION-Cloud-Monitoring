package integration;

import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.VismoService;
import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.sources.BasicEventSource;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;

import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;


/**
 * 
 */
public class VismoServiceTest {
    /***/
    private VismoService      service;
    /***/
    private final VismoVMInfo vminfo = new VismoVMInfo();


    /***/
    @Test
    public void itShouldStartProperly() {
        service.start();

        final int pid = Integer.parseInt(service.status());

        assertTrue(pid > 0);
    }


    /**
     * 
     */
    @Test
    public void itShouldStopProperly() {
        final ZMQSockets zmq = new ZMQSockets(new ZContext());
        final String address = "tcp://127.0.0.1:56780";

        service.addTask(new BasicEventSource(zmq.newBoundPullSocket(address), zmq.newConnectedPushSocket(address)));

        service.start();
        sleep(2000);
        service.stop();
    }


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        service = new VismoService(vminfo);
    }


    /**
     * @param n
     */
    private static void sleep(final long n) {
        try {
            Thread.sleep(n);
        } catch (final InterruptedException ignored) {
            //
        }
    }
}
