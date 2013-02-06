package gr.ntua.vision.monitoring.mon;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to create and add new group members to a {@link GroupMembership}.
 */
public class AddGroupMember implements GroupNotification {
    /** the log target. */
    private static final Logger   log = LoggerFactory.getLogger(AddGroupMember.class);
    /***/
    private final GroupMembership mship;


    /**
     * Constructor.
     * 
     * @param mship
     */
    public AddGroupMember(final GroupMembership mship) {
        this.mship = mship;
    }


    /**
     * @see gr.ntua.vision.monitoring.mon.GroupNotification#pass(java.net.InetAddress, java.lang.String)
     */
    @Override
    public void pass(final InetAddress addr, final String str) {
        log.trace("from {} received {}", addr, str);
        mship.add(buildFromString(addr, str));
    }


    /**
     * @param addr
     * @param s
     * @return a {@link GroupElement}.
     */
    @SuppressWarnings("static-method")
    private GroupElement buildFromString(final InetAddress addr, final String s) {
        return new GroupElement(addr, s);
    }
}
