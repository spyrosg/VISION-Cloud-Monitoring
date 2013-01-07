package gr.ntua.vision.monitoring.rules.propagation;

import gr.ntua.vision.monitoring.threading.PeriodicTask;

import java.util.Iterator;
import java.util.Timer;


/**
 * @author tmessini
 */
public class RulesPropagationWatchDog extends PeriodicTask {

    /***/
    private RulesPropagationManager manager;
    /***/
    private final long              maxtime = 10000;
    /***/
    // private final static Logger log = LoggerFactory.getLogger(RulesPropagationWatchDog.class);
    /***/
    private final long              period;


    /**
     * Constructor.
     * 
     * @param period
     */
    public RulesPropagationWatchDog(final long period) {
        this.period = period;
    }


    @Override
    public void run() {// import org.slf4j.Logger;
        // import org.slf4j.LoggerFactory;
        updateMessageMembership(maxtime);
    }


    @Override
    public void scheduleWith(final Timer timer) {
        timer.schedule(this, 0, period);
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
        // log.info("timestamp size: "+manager.getMessageTimestamp().getSize());
        if (manager.getMessageTimestamp() != null) {
            final Iterator<Message> iterator = manager.getMessageTimestamp().keys().iterator();
            while (iterator.hasNext()) {
                final Message msg = iterator.next();
                if (!isActive(maxtime, msg)) {
                    // log.info("watchdog activated!");

                    /*
                    log.info(manager.getPid() 
                             +": message with commandId: " 
                             + msg.getCommandId() 
                             + " inactive for: "
                             + getTimestampDelta(msg)
                             + " > dropped!");
                     */
                    // log.info(manager.getMessageCounter().getMessageValue(msg).toString());

                    manager.getMessageTimestamp().remove(msg);
                    manager.getMessageCounter().remove(msg);

                    // trim size of Arrays
                    // log.info(manager.getPid()+" trim queues sizes!");
                    manager.getOutQueue().trimSize();
                    manager.getInQueue().trimSize();
                    manager.getDelQueue().trimSize();

                }

            }
        }

    }

}