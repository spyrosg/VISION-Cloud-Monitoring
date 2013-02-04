package unit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.mon.GroupElement;
import gr.ntua.vision.monitoring.mon.GroupMembership;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.TimerTask;

import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class GroupMembershipTest {
    /***/
    private static final long                      EXPIRATION_PERIOD = 100;
    /***/
    private final HashMap<GroupElement, TimerTask> map               = new HashMap<GroupElement, TimerTask>();
    /***/
    private GroupMembership                        mship;


    /**
     * @throws UnknownHostException
     */
    @Test
    public void afterExpirationMemberShouldBeenRemoved() throws UnknownHostException {
        final GroupElement m = new GroupElement(InetAddress.getLocalHost());

        mship.add(m);
        assertEquals(1, map.size());
    }


    /**
     * @throws UnknownHostException
     * @throws InterruptedException
     */
    @Test
    public void membershipShouldSupportUniqueMembers() throws UnknownHostException, InterruptedException {
        final GroupElement m = new GroupElement(InetAddress.getLocalHost());

        mship.add(m);
        mship.add(m);

        assertTrue(map.containsKey(m));
        Thread.sleep(2 * EXPIRATION_PERIOD); // wait for element to expire.
        assertFalse(map.containsKey(m));
    }


    /***/
    @Before
    public void setUp() {
        mship = new GroupMembership(EXPIRATION_PERIOD, map);
    }


    /**
     * @throws UnknownHostException
     * @throws InterruptedException
     */
    @Test
    public void shouldNotRemoveUpdatedMember() throws UnknownHostException, InterruptedException {
        final long ts = System.currentTimeMillis();
        final GroupElement m1 = new GroupElement(InetAddress.getLocalHost(), ts);
        final GroupElement m2 = new GroupElement(InetAddress.getLocalHost(), ts + 10);

        assertEquals("elements should be identical", m1, m2);

        mship.add(m1);
        Thread.sleep(EXPIRATION_PERIOD / 2); // wait just a bit before updating
        assertTrue("element should still be in group", map.containsKey(m1));
        mship.add(m2); // update group
        Thread.sleep(4 * EXPIRATION_PERIOD / 5); // wait for removal of old member.
        assertTrue("element should not have been removed yet", map.containsKey(m2));
    }
}
