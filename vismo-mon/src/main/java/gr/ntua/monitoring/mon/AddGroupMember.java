package gr.ntua.monitoring.mon;

import java.net.UnknownHostException;


/**
 * 
 */
class AddGroupMember implements GroupNotification {
    /***/
    private final GroupElementFactory factory;
    /***/
    private final GroupMembership     mship;


    /**
     * Constructor.
     * 
     * @param factory
     * @param mship
     */
    public AddGroupMember(final GroupElementFactory factory, final GroupMembership mship) {
        this.factory = factory;
        this.mship = mship;
    }


    /**
     * @see gr.ntua.monitoring.mon.GroupNotification#pass(java.lang.String)
     */
    @Override
    public void pass(final String notification) {
        if (notification == null)
            return;

        try {
            mship.add(parseNotification(notification));
        } catch (final UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param notification
     * @return a new {@link GroupElement}.
     * @throws UnknownHostException
     */
    private GroupElement parseNotification(final String notification) throws UnknownHostException {
        return factory.buildFromString(notification);
    }
}
