package gr.ntua.vision.monitoring.rules.propagation.store;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * this class stores the rules catalog.
 * 
 * @author tmessini
 */
public class ClusterRuleStore {
    /***/
    @SuppressWarnings("unused")
    private final static Logger                                               log = LoggerFactory
                                                                                          .getLogger(ClusterRuleStore.class);
    /***/
    private final ConcurrentHashMap<ConcurrentHashMap<Integer, String>, Long> clusterRulesSetTimestamped;


    /**
     * Constructor
     */
    public ClusterRuleStore() {
        clusterRulesSetTimestamped = new ConcurrentHashMap<ConcurrentHashMap<Integer, String>, Long>();
    }


    /**
     * @param nodeRuleSet
     * @param updateDiff
     */
    public void addNodeRuleSet(final ConcurrentHashMap<Integer, String> nodeRuleSet, final long updateDiff) {
        if (clusterRulesSetTimestamped.get(nodeRuleSet) == null)
            clusterRulesSetTimestamped.put(nodeRuleSet, updateDiff);
        else {
            final long diff = clusterRulesSetTimestamped.get(nodeRuleSet);
            if (updateDiff < diff)
                clusterRulesSetTimestamped.put(nodeRuleSet, updateDiff);
        }
    }


    /**
     * clears the cluster rules store
     */
    public void clearClusterRuleStore() {
        clusterRulesSetTimestamped.clear();
    }


    /**
     * @return clusterRulesSetTimestamped
     */
    public ConcurrentHashMap<ConcurrentHashMap<Integer, String>, Long> getClusterRuleStore() {
        return clusterRulesSetTimestamped;
    }

}
