package gr.ntua.vision.monitoring.rules.propagation.services;

import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.rules.propagation.message.Message;
import gr.ntua.vision.monitoring.threading.PeriodicTask;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class WatchDog extends PeriodicTask {
    /***/
    private static final Logger     log     = LoggerFactory.getLogger(WatchDog.class);
    /***/
    private RulesPropagationManager manager;
    /***/
    private final long              maxtime = 10000;


    /**
     * Constructor.
     * 
     * @param period
     */
    public WatchDog(final long period) {
        super(period);
    }


    /**
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        discardMessages(maxtime);
    }


    /**
     * @param manager
     */
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
    }


    /**
     * remove message from processing.
     * 
     * @param maxtime
     */
    private void discardMessages(final long maxtime) {
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

}
