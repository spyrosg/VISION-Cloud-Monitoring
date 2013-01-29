package gr.ntua.vision.monitoring.mon;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A {@link GroupMembership} is just a collection of {@link GroupElement}s, where these elements are maintained for a predefined
 * period of time before being removed.
 */
public class GroupMembership {
    /** the expiration period for group membership. */
    private final long              expirationPeriod;
    /** the actual group. */
    private final Set<GroupElement> set;
    /** the timer used to schedule removals */
    private final Timer             timer = new Timer(true);


    /**
     * Constructor.
     * 
     * @param expirationPeriod
     *            the expiration period for group membership.
     */
    public GroupMembership(final long expirationPeriod) {
        this(expirationPeriod, new LinkedHashSet<GroupElement>());
    }


    /**
     * Constructor.
     * 
     * @param expirationPeriod
     *            the expiration period for group membership.
     * @param set
     *            a group elements set.
     */
    public GroupMembership(final long expirationPeriod, final Set<GroupElement> set) {
        this.expirationPeriod = expirationPeriod;
        this.set = set;
    }


    /**
     * Add an element to the group, if it is not already contained in the group.
     * 
     * @param elem
     *            the element.
     */
    public void add(final GroupElement elem) {
        if (set.add(elem))
            scheduleRemoval(elem);
    }


    /**
     * This is used to abstract away the access to elements of the group. The given action is performed on each element of the
     * group.
     * 
     * @param proc
     *            the action to apply.
     */
    public void forEach(final GroupProc proc) {
        for (final GroupElement member : set)
            proc.applyTo(member);
    }


    /**
     * @param elem
     */
    void remove(final GroupElement elem) {
        set.remove(elem);
    }


    /**
     * @param elem
     */
    private void scheduleRemoval(final GroupElement elem) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                remove(elem);
            }
        }, expirationPeriod);
    }
}
