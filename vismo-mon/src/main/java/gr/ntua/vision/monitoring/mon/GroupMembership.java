package gr.ntua.vision.monitoring.mon;

import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to keep in memory the members of a group.
 */
public class GroupMembership {
    /** the log target. */
    private static final Logger     log = LoggerFactory.getLogger(GroupMembership.class);
    /** the actual members. */
    private final Set<GroupElement> members;


    /**
     * Constructor.
     */
    public GroupMembership() {
        this.members = new LinkedHashSet<GroupElement>();
    }


    /**
     * Constructor.
     * 
     * @param members
     *            the initial set of members.
     */
    public GroupMembership(final Set<GroupElement> members) {
        this.members = members;
    }


    /**
     * Add an element to the group.
     * 
     * @param elem
     *            the element to add.
     */
    public void add(final GroupElement elem) {
        log.debug("adding new member: {}", elem);
        members.add(elem);
    }


    /**
     * Check whether the element is a member of <code>this</code>.
     * 
     * @param elem
     *            the element.
     * @return <code>true</code> iff <code>member</code> is member of <code>this</code> group, <code>false</code> otherwise.
     */
    public boolean contains(final GroupElement elem) {
        return members.contains(elem);
    }


    /**
     * Perform the given action on each member of this group.
     * 
     * @param action
     *            the action to perform.
     */
    public void forEach(final GroupProc action) {
        for (final GroupElement member : members)
            action.performWith(member);
    }
}
