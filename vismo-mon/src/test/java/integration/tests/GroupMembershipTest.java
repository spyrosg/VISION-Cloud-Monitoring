package integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.mon.GroupElement;
import gr.ntua.vision.monitoring.mon.GroupMembership;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;

import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class GroupMembershipTest {
    /** the expiration period for the group, in milliseconds. */
    private static final long                 EXPIRATION_PERIOD = 1000;
    /***/
    private GroupMembership                   mship;
    /***/
    private final LinkedHashSet<GroupElement> set               = new LinkedHashSet<GroupElement>();


    /**
     * @throws UnknownHostException
     * @throws InterruptedException
     */
    @Test
    public void membershipShouldExpireAfterSpecifiedPeriod() throws UnknownHostException, InterruptedException {
        final GroupElement m = new GroupElement("id", InetAddress.getLocalHost());

        mship.add(m);
        Thread.sleep(500); // wait a bit but before expiration
        assertTrue(set.contains(m));
        Thread.sleep(600); // let it expire
        assertFalse(set.contains(m));
    }


    /***/
    @Before
    public void setUp() {
        mship = new GroupMembership(EXPIRATION_PERIOD, set);
    }


    /**
     * @throws UnknownHostException
     */
    @Test
    public void shouldNotAcceptIdenticalElements() throws UnknownHostException {
        final long ts = System.currentTimeMillis();
        final GroupElement m1 = new GroupElement("id", InetAddress.getLocalHost(), ts);
        final GroupElement m2 = new GroupElement("id", InetAddress.getLocalHost(), ts);

        mship.add(m1);
        mship.add(m2); // m2 should be ignored

        assertEquals(1, set.size());
        assertTrue(set.contains(m1));
    }
}
