package endtoend.tests;

import static org.junit.Assert.assertThat;
import gr.ntua.monitoring.mon.GroupClient;
import gr.ntua.monitoring.mon.VismoGroupServer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class VismoGroupTest {
    /**
     * A hamcrest matcher for comparing in order arrays.
     * 
     * @param <T>
     *            the type of the elements of the sequence.
     */
    public static class ArrayInOrderMatcher<T> extends TypeSafeMatcher<Iterable<T>> {
        /***/
        private final T[] arr;


        /**
         * @param arr
         */
        public ArrayInOrderMatcher(final T... arr) {
            this.arr = arr;
        }


        /**
         * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
         */
        @Override
        public void describeTo(final Description desc) {
            desc.appendValue(arr);
        }


        /**
         * @see org.junit.matchers.TypeSafeMatcher#matchesSafely(java.lang.Object)
         */
        @Override
        public boolean matchesSafely(final Iterable<T> iter) {
            int i = 0;

            for (final T t : iter)
                if (!arr[i++].equals(t))
                    return false;

            return true;
        }


        /**
         * @param arr
         * @return an {@link ArrayInOrderMatcher}.
         */
        public static <T> ArrayInOrderMatcher<T> contains(final T... arr) {
            return new ArrayInOrderMatcher<T>(arr);
        }
    }
    /***/
    private static final String         GROUP_ADDRESS = "235.1.1.1";
    /***/
    private static final int            GROUP_PORT    = 12346;
    /***/
    private final GroupClient[]         clients       = new GroupClient[3];
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
        assertThat(notifications, ArrayInOrderMatcher.contains("ohai-0", "ohai-1", "ohai-2"));
    }
}
