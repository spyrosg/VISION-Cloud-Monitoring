package integration.tests;

import static org.junit.Assert.assertThat;
import gr.ntua.vision.monitoring.mon.GroupClient;
import gr.ntua.vision.monitoring.mon.GroupServer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class GroupTest {
    /***/
    private static final String GROUP_ADDRESS = "235.1.1.1";
    /***/
    private static final int    GROUP_PORT    = 12346;
    /***/
    final LinkedHashSet<String> notifications = new LinkedHashSet<String>();
    /***/
    private final GroupClient[] clients       = new GroupClient[3];
    /***/
    private GroupServer         server;
    /***/
    private Thread              t;


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void serverShouldPickUpNewNotificationsAsTheyComeIn() throws IOException, InterruptedException {
        Thread.sleep(100); // spin server thread

        for (int i = 0; i < clients.length; ++i)
            clients[i].notifyGroup("ohai-" + i);

        Thread.sleep(100); // wait for all notifications to arrive.
        assertThatServerReceivedNotificationsAsTheyComeIn();
    }


    /**
     * @throws UnknownHostException
     */
    @Before
    public void setUp() throws UnknownHostException {
        for (int i = 0; i < clients.length; ++i)
            clients[i] = new GroupClient(GROUP_ADDRESS, GROUP_PORT);

        server = new GroupServer(GROUP_ADDRESS, GROUP_PORT) {
            @Override
            protected void notify(final String notification) {
                notifications.add(notification);
            }
        };
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
        assertThat(notifications, ArrayInOrderMatcher.contains("ohai-0", "ohai-1", "ohai-2"));
    }
}
