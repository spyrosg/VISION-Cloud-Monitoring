package gr.ntua.vision.monitoring.rules.propagation;

import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * this class stores the rules catalog.
 * 
 * @author tmessini
 */
public class ClusterRuleStore  extends Observable {     
    /***/
    private final ConcurrentHashMap<ConcurrentHashMap<Integer, String>, Long> clusterRulesSetTimestamped;
    /***/
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

}
