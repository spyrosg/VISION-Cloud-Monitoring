package gr.ntua.vision.monitoring.mon;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A {@link GroupMembership} is just a collection of {@link GroupElement}s, where there are maintained for a predefined period,
 * before being removed. If a newer (more up-to-date) element comes in where an older one exists, the older one is removed and its
 * place taken by the new arrival.
 */
public class GroupMembership {
    /** the expiration period for group membership, specified in millies. */
    private final long                         expirationPeriod;
    /** the actual group. */
    private final Map<GroupElement, TimerTask> members;
    /** the timer used to schedule removals */
    private final Timer                        timer = new Timer(true);


    /**
     * Constructor.
     * 
     * @param expirationPeriod
     *            the expiration period for group membership, specified in millies.
     */
    public GroupMembership(final long expirationPeriod) {
        this(expirationPeriod, new HashMap<GroupElement, TimerTask>());
    }


    /**
     * Constructor.
     * 
     * @param expirationPeriod
     *            the expiration period for group membership, specified in millies.
     * @param members
     *            a group's members.
     */
    public GroupMembership(final long expirationPeriod, final Map<GroupElement, TimerTask> members) {
        this.expirationPeriod = expirationPeriod;
        this.members = members;
    }


    /**
     * Add an element to the group. If an identical already exists, this is an update operation (i.e. it removes the old element
     * and adds the new one).
     * 
     * @param elem
     *            the element.
     */
    public void add(final GroupElement elem) {
        if (members.containsKey(elem)) { // an identical element already exists
            removeById(elem);
            addById(elem);
        } else
            addById(elem);
    }


    /**
     * This is used to abstract away the access to elements of the group. The given action is performed on each element of the
     * group.
     * 
     * @param proc
     *            the action to apply.
     */
    public void forEach(final GroupProc proc) {
        for (final GroupElement member : members.keySet())
            proc.applyTo(member);
    }


    /**
     * Remove the member from the group. Cancel its timer.
     * 
     * @param member
     *            the member.
     */
    void removeById(final GroupElement member) {
        final TimerTask task = members.remove(member);

        task.cancel();
    }


    /**
     * Add a new member to the group. Schedule its removal.
     * 
     * @param elem
     *            the element.
     */
    private void addById(final GroupElement elem) {
        final TimerTask task = removalTask(members, elem);

        members.put(elem, task);
        timer.schedule(task, expirationPeriod);
    }


    /**
     * Get a timer task for removing an element from a map.
     * 
     * @param map
     *            the map to remove from.
     * @param elem
     *            the element to remove.
     * @return a {@link TimerTask}.
     */
    private static TimerTask removalTask(final Map<GroupElement, TimerTask> map, final GroupElement elem) {
        return new TimerTask() {
            @Override
            public void run() {
                map.remove(elem);
            }
        };
    }
}
