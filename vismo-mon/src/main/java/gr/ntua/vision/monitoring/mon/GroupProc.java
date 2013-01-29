package gr.ntua.vision.monitoring.mon;

/**
 * An abstraction for doing stuff on each member of a {@link GroupMembership}.
 */
public interface GroupProc {
    /**
     * Apply the operation to the member.
     * 
     * @param member
     *            the member.
     */
    void applyTo(GroupElement member);
}
