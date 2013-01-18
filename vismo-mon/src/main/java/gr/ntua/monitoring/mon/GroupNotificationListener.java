package gr.ntua.monitoring.mon;

/**
 * Interface to be implemented by anyone interested in receiving group notifications.
 */
public interface GroupNotificationListener {
    /**
     * Pass the notification.
     * 
     * @param notification
     *            the notification.
     */
    void pass(String notification);
}
