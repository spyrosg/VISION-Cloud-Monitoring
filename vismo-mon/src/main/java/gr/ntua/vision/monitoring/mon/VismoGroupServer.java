package gr.ntua.vision.monitoring.mon;

import gr.ntua.vision.monitoring.VismoConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


/**
 * This is used to pass notifications received from the vismo group to registered listeners.
 */
public class VismoGroupServer extends GroupServer {
    /** the listeners to notify. */
    private final ArrayList<GroupNotification> listeners = new ArrayList<GroupNotification>();


    /**
     * Constructor.
     * 
     * @param conf
     *            the configuration object.
     * @throws UnknownHostException
     */
    public VismoGroupServer(final VismoConfiguration conf) throws UnknownHostException {
        super(conf.getMonGroupAddr(), conf.getMonGroupPort());
    }


    /**
     * Register a listener to the group notifications.
     * 
     * @param listener
     *            the listener.
     */
    public void register(final GroupNotification listener) {
        listeners.add(listener);
    }


    /**
     * @see gr.ntua.vision.monitoring.mon.GroupServer#notify(java.net.InetAddress, java.lang.String)
     */
    @Override
    protected void notify(final InetAddress addr, final String notification) {
        notifyListeners(addr, notification);

    }


    /**
     * @param addr
     * @param notification
     */
    private void notifyListeners(final InetAddress addr, final String notification) {
        for (final GroupNotification listener : listeners)
            listener.pass(addr, notification);
    }
}
