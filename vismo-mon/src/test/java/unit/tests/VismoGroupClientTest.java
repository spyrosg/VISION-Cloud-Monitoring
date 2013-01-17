package unit.tests;

import static org.junit.Assert.assertTrue;
import gr.ntua.monitoring.mon.VismoGroupClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class VismoGroupClientTest {
    /***/
    private static final String GROUP_ADDRESS        = "228.5.6.7";
    /***/
    private static final int    GROUP_PORT           = 12345;
    /***/
    private static final String NOTE                 = "ping?";
    /***/
    final AtomicBoolean         receivedNotification = new AtomicBoolean();
    /***/
    private Thread              t;


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void notificationShouldBeReceivedByGroup() throws IOException, InterruptedException {
        final VismoGroupClient client = new VismoGroupClient(GROUP_ADDRESS, GROUP_PORT);

        Thread.sleep(100); // spin thread
        client.notifyGroup(NOTE);
        Thread.sleep(100); // wait for notification to arrive
        assertTrue(receivedNotification.get());
    }


    /***/
    @Before
    public void setUp() {
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    waitReceiveLoop();
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }


            private void waitReceiveLoop() throws Exception {
                final MulticastSocket sock = new MulticastSocket(new InetSocketAddress(GROUP_PORT));
                final InetAddress addr = InetAddress.getByName(GROUP_ADDRESS);

                System.err.println("joining group");
                sock.joinGroup(addr);

                while (!Thread.currentThread().isInterrupted()) {
                    final byte buf[] = new byte[256];
                    final DatagramPacket pack = new DatagramPacket(buf, buf.length);

                    System.err.println("waiting to receive...");
                    sock.receive(pack);

                    final String message = new String(pack.getData(), 0, pack.getLength());
                    System.err.println("received " + message);

                    if (NOTE.equals(message))
                        receivedNotification.set(true);
                }

                System.err.println("leaving group");
                sock.leaveGroup(addr);
            }
        }, "waiting-notifications");
        t.setDaemon(true);
        t.start();
    }


    /***/
    @After
    public void tearDown() {
        if (t != null)
            t.interrupt();
    }
}
