package integration.tests;

import static org.junit.Assert.assertEquals;
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
    private GroupMembership                   mship;
    /***/
    private final LinkedHashSet<GroupElement> set = new LinkedHashSet<GroupElement>();


    /**
     * @throws UnknownHostException
     * @throws InterruptedException
     */
    @Test
    public void membershipShouldMaintainOnlyTheMostRecentlyUpdatedMembers() throws UnknownHostException, InterruptedException {
        final String ID = "id1";
        final GroupElement m1;
        final GroupElement m2;

        mship.add(m1 = new GroupElement(ID, InetAddress.getLocalHost()));
        Thread.sleep(100); // wait for newer identical entry to come in.
        mship.add(m2 = new GroupElement(ID, InetAddress.getLocalHost()));

        assertEquals(1, mship.size());
        assertTrue("m2 should be a member since is more up-to-date", mship.contains(m2));
    }


    /***/
    @Before
    public void setUp() {
        mship = new GroupMembership(set);
    }
}
