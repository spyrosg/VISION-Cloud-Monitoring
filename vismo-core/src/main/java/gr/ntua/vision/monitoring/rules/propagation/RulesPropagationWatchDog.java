package gr.ntua.vision.monitoring.rules.propagation;

import gr.ntua.vision.monitoring.threading.PeriodicTask;

import java.util.Iterator;


/**
 * @author tmessini
 */
public class RulesPropagationWatchDog extends PeriodicTask {

    /***/
    private RulesPropagationManager manager;
    /***/
    private final long              maxtime = 10000;


    /**
     * Constructor.
     * 
     * @param period
     */
    public RulesPropagationWatchDog(final long period) {
        super(period);
    }


    @Override
    public void run() {
        updateMessageMembership(maxtime);
    }


    /**
     * @param manager
     */
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
    }


    /**
     * @param msg
     * @return long
     */
    private long getTimestampDelta(final Message msg) {
        final long delta = System.currentTimeMillis() - manager.getMessageTimestamp().getMessageValue(msg);
        return delta;
    }


    /**
     * Decide whether the message is active based on previous received timestamps.
     * 
     * @param maxtime
     * @param msg
     * @return if the host is still active.
     */
    private boolean isActive(final long maxtime, final Message msg) {
        return getTimestampDelta(msg) < maxtime;

    }


    /**
     * remove message from processing.
     * 
     * @param maxtime
     */
    private void updateMessageMembership(final long maxtime) {
        if (manager.getMessageTimestamp() != null) {
            final Iterator<Message> iterator = manager.getMessageTimestamp().keys().iterator();
            while (iterator.hasNext()) {
                final Message msg = iterator.next();
                if (!isActive(maxtime, msg)) {
                    manager.getMessageTimestamp().remove(msg);
                    manager.getMessageCounter().remove(msg);
                    manager.getOutQueue().trimSize();
                    manager.getInQueue().trimSize();
                    manager.getDelQueue().trimSize();

                }

            }
        }

    }

}
