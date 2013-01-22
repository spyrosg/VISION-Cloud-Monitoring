package gr.ntua.vision.monitoring.mon;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to keep in memory the members of a group. {@link GroupMembership}s behave like sets with two main differences:
 * <ol>
 * <li>Each member has an expiration period, after which it gets evicted.</li>
 * <li>When inserting an already present member, that member is updated.</li>
 * </ol>
 */
public class GroupMembership {
    /** the log target. */
    private static final Logger     log   = LoggerFactory.getLogger(GroupMembership.class);
    /** the expiration period for the group membership. */
    private final long              expirationPeriod;
    /** the actual members. */
    private final Set<GroupElement> set;
    /** the timer used to schedule removals. */
    private final Timer             timer = new Timer(true);


    /**
     * Constructor.
     * 
     * @param expirationPeriod
     */
    public GroupMembership(final long expirationPeriod) {
        this(expirationPeriod, new LinkedHashSet<GroupElement>());
    }


    /**
     * Constructor.
     * 
     * @param expirationPeriod
     * @param members
     *            the initial set of members.
     */
    public GroupMembership(final long expirationPeriod, final Set<GroupElement> members) {
        this.expirationPeriod = expirationPeriod;
        this.set = members;
    }


    /**
     * Add an element to the group. If an identical element is already contained, it is replaced with the new one, by the
     * assumption that the new one in the most up-to-date.
     * 
     * @param e
     *            the element to add.
     */
    public void add(final GroupElement e) {
        log.debug("adding new member: {}", e);
        addElement(e);
    }


    /**
     * Perform the given action on each member of this group.
     * 
     * @param action
     *            the action to perform.
     */
    public void forEach(final GroupProc action) {
        for (final GroupElement member : set)
            action.performWith(member);
    }


    /**
     * @param elem
     */
    void removeElement(final GroupElement elem) {
        set.remove(elem);
    }


    /**
     * @param elem
     */
    private void addElement(final GroupElement elem) {
        set.add(elem);
        scheduleForRemoval(elem);
    }


    /**
     * @param elem
     */
    private void scheduleForRemoval(final GroupElement elem) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                removeElement(elem);
            }
        }, expirationPeriod);
    }
}
