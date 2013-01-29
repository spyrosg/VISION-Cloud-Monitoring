package gr.ntua.vision.monitoring.mon;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to create and add new group members to a {@link GroupMembership}.
 */
public class AddGroupMember implements GroupNotification {
    /***/
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
     * @param s
     * @return a {@link GroupElement}.
     * @throws UnknownHostException
     */
    @SuppressWarnings("static-method")
    public GroupElement buildFromString(final String s) throws UnknownHostException {
        final String[] fields = s.split(":");

        return new GroupElement(fields[0], InetAddress.getByName(fields[1]));
    }


    /**
     * @see gr.ntua.vision.monitoring.mon.GroupNotification#pass(java.lang.String)
     */
    @Override
    public void pass(final String str) {
        try {
            final GroupElement elem = buildFromString(str);

            mship.add(elem);
        } catch (final UnknownHostException e) {
            log.error("cannot parse notification: " + str, e);
        }
    }
}
