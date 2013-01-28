package gr.ntua.vision.monitoring.rules.propagation.services;

import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.rules.propagation.message.MessageFactory;
import gr.ntua.vision.monitoring.rules.propagation.message.MessageType;
import gr.ntua.vision.monitoring.threading.PeriodicTask;

import java.util.Collections;
import java.util.Iterator;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class ClusterRulesResolver extends PeriodicTask {
    /***/
    final Logger                                        log                 = LoggerFactory.getLogger(ClusterRulesResolver.class);
    /***/
    private final long                                  accepetable_diff    = 500;
    /***/
    private final RulesPropagationManager               manager;
    /***/
    private final MessageFactory                        messageFactory;
    /***/
    private final long                                  period;
    /***/
    private volatile ConcurrentHashMap<Integer, String> validClusterRuleSet = null;
    /***/
    private volatile long                               validTimestamp      = 0;


    /**
     * Constructor.
     * 
     * @param period
     * @param manager
     */
    public ClusterRulesResolver(final long period, final RulesPropagationManager manager) {
        this.period = period;
        this.manager = manager;
        messageFactory = new MessageFactory(manager);
    }


    /**
     * @return rulesClusterSet
     */
    public ConcurrentHashMap<Integer, String> getValidClusterRuleSet() {
        return validClusterRuleSet;
    }


    /**
     * @return valid timestamp
     */
    public long getValidTimestamp() {
        return validTimestamp;
    }


    @Override
    public void run() {
        rulesSynchronization();
    }


    @Override
    public void scheduleWith(final Timer timer) {
        timer.schedule(this, 0, period);
    }


    /**
     * @param validClusterRuleSet
     */
    public void setValidClusterRuleSet(final ConcurrentHashMap<Integer, String> validClusterRuleSet) {
        this.validClusterRuleSet = validClusterRuleSet;
    }


    /**
     * @param validTimestamp
     */
    public void setValidTimestamp(final long validTimestamp) {
        this.validTimestamp = validTimestamp;
    }


    /**
     * Rules defines the wining rule set is the one with the biggest SET of rules which has been updated most lately in a period.
     */
    private void clusterRulesResolver() {
        if (manager.isElected()) {
            final ConcurrentHashMap<ConcurrentHashMap<Integer, String>, Long> ruleSet = manager.getClusterRuleStore()
                    .getClusterRuleStore();
            log.info("RuleSet:" + ruleSet);
            if (ruleSet.size() != 0) {
                final long min = Collections.min(ruleSet.values());
                final long maxTimestampDiff = min + accepetable_diff;
                System.out.println(ruleSet.toString());
                System.out.println("min:" + min + " max:" + maxTimestampDiff);
                ConcurrentHashMap<Integer, String> validRuleSet = null;
                long time = Long.MAX_VALUE;
                int maxRuleSetSize = 0;
                final Iterator<ConcurrentHashMap<Integer, String>> iter = ruleSet.keySet().iterator();
                while (iter.hasNext()) {
                    final ConcurrentHashMap<Integer, String> key = iter.next();
                    if (ruleSet.get(key) < maxTimestampDiff && key.size() > maxRuleSetSize) {
                        validRuleSet = key;
                        maxRuleSetSize = key.size();
                        time = ruleSet.get(key);
                    }
                }
                setValidClusterRuleSet(validRuleSet);
                setValidTimestamp(time);
            }
        }

    }


    /**
     * this method triggers the resolution.
     */
    private void rulesSynchronization() {
        if (manager.isElected()) {
            // send the set_rules message
            manager.getOutQueue().addMessage(messageFactory.createMessage(MessageType.SET_RULES));
            // the algorithm for resolution.
            clusterRulesResolver();
            // start the process from start
            manager.getClusterRuleStore().clearClusterRuleStore();
            // send the get_gules message
            manager.getOutQueue().addMessage(messageFactory.createMessage(MessageType.GET_RULES));
        }
    }

}
