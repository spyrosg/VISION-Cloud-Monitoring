package gr.ntua.vision.monitoring.mon;

/**
 * An abstraction for doing stuff on each member of a {@link GroupMembership}.
 */
public interface GroupProc {
    /**
     * @param member
     */
    void performWith(final GroupElement member);
}
