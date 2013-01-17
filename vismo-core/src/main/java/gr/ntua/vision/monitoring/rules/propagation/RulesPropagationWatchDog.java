package gr.ntua.vision.monitoring.rules.propagation;

import gr.ntua.vision.monitoring.threading.PeriodicTask;

import java.util.Iterator;
import java.util.Timer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class RulesPropagationWatchDog extends PeriodicTask {

    /***/
    final Logger                                   log = LoggerFactory.getLogger(RulesPropagationWatchDog.class);
    /***/
    private RulesPropagationManager manager;
    /***/
    private final long              maxtime = 10000;
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
    public void run() {
        startRulesSynchronization();
        discardMessages(maxtime);
    }

    /**
     * 
     */    
    private void startRulesSynchronization() {               
        if (manager.isElected()) {
            log.info("send get-rules " +
            		"message");
            manager.getRulesResolutionQueue().clear();
            manager.getRulesClusterSynchronizer().clearRuleSet();            
            if(manager.getRulesResolutionQueue().isQEmpty())
            {
            final Message m = new Message();
            m.setGroupSize(1);
            m.setCommandId(manager.getRandomID());
            m.setType(MessageType.GET_RULES);
            m.setCommand("");
            manager.getOutQueue().addMessage(m);
            }
        }        
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
    private void discardMessages(final long maxtime) {
        /*
        log.info("message timestamp size: {}",manager.getMessageTimestamp().getSize());
        log.info("message timestamp counter: {}",manager.getMessageCounter().getSize());
        log.info("message timestamp outqueue: {}",manager.getOutQueue().getSize());
        log.info("message timestamp size: {}",manager.getInQueue().getSize());
        log.info("message timestamp size: {}",manager.getDelQueue().getSize());
        */
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
