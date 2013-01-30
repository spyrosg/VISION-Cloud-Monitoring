package unit.tests;

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
    /***/
    private static final long                 EXPIRATION_PERIOD = 100;
    /***/
    private GroupMembership                   mship;
    /***/
    private final LinkedHashSet<GroupElement> set               = new LinkedHashSet<GroupElement>();


    /**
     * @throws UnknownHostException
     */
    @Test
    public void afterExpirationMemberShouldBeenRemoved() throws UnknownHostException {
        final GroupElement m = new GroupElement(InetAddress.getLocalHost());

        mship.add(m);
        assertEquals(1, set.size());
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

        assertTrue(set.contains(m));
        Thread.sleep(2 * EXPIRATION_PERIOD); // wait for element to expire.
        assertFalse(set.contains(m));
    }


    /***/
    @Before
    public void setUp() {
        mship = new GroupMembership(EXPIRATION_PERIOD, set);
    }
}
