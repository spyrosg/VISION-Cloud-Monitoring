package gr.ntua.vision.monitoring.mon;

import java.net.InetAddress;


/**
 * Interface to be implemented by anyone interested in receiving group notifications.
 */
public interface GroupNotification {
    /**
     * Pass the notification.
     * 
     * @param addr
     *            the address of the machine in the group this notification has been received from.
     * @param notification
     *            the notification.
     */
    void pass(InetAddress addr, String notification);
}
