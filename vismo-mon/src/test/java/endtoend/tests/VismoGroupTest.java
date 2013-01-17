package endtoend.tests;

import static org.junit.Assert.assertTrue;
import gr.ntua.monitoring.mon.VismoGroupClient;
import gr.ntua.monitoring.mon.VismoGroupServer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class VismoGroupTest {
    /***/
    private static final String         GROUP_ADDRESS = "235.1.1.1";
    /***/
    private static final int            GROUP_PORT    = 12346;
    /***/
    private final VismoGroupClient[]    clients       = new VismoGroupClient[3];
    /***/
    private final LinkedHashSet<String> notifications = new LinkedHashSet<String>();
    /***/
    private VismoGroupServer            server;
    /***/
    private Thread                      t;


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void serverShouldPickUpNewNotificationsAsTheyComeIn() throws IOException, InterruptedException {
        Thread.sleep(100); // spin thread

        for (int i = 0; i < clients.length; ++i)
            clients[i].notifyGroup("ohai-" + i);

        Thread.sleep(1000); // wait for all notifications to arrive.
        assertThatServerReceivedNotificationsAsTheyComeIn();
    }


    /**
     * @throws UnknownHostException
     */
    @Before
    public void setUp() throws UnknownHostException {
        for (int i = 0; i < clients.length; ++i)
            clients[i] = new VismoGroupClient(GROUP_ADDRESS, GROUP_PORT);

        server = new VismoGroupServer(GROUP_ADDRESS, GROUP_PORT, notifications);
        t = new Thread(server, "vismo-group-server");
        t.setDaemon(true);
        t.start();
    }


    /***/
    @After
    public void tearDown() {
        if (t != null)
            t.interrupt();
    }


    /***/
    private void assertThatServerReceivedNotificationsAsTheyComeIn() {
        assertTrue(containsInOrder(notifications, "ohai-0", "ohai-1", "ohai-2"));
    }


    /**
     * Assert that iterable contains in order the elements in the array.
     * 
     * @param iter
     * @param arr
     * @return <code>true</code> iff iter has the same elements in order with array, <code>false</code> otherwise.
     */
    private static <T> boolean containsInOrder(final Iterable<T> iter, final T... arr) {
        int i = 0;

        for (final T t : iter)
            if (!arr[i++].equals(t))
                return false;

        return true;
    }
}
