package gr.ntua.monitoring.mon;

import gr.ntua.vision.monitoring.VismoConfiguration;

import java.net.UnknownHostException;
import java.util.ArrayList;


/**
 *
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
     * @see gr.ntua.monitoring.mon.GroupServer#notify(java.lang.String)
     */
    @Override
    protected void notify(final String notification) {
        notifyListeners(notification);
    }


    /**
     * @param notification
     */
    private void notifyListeners(final String notification) {
        for (final GroupNotification listener : listeners)
            listener.pass(notification);
    }
}
