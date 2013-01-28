package gr.ntua.vision.monitoring.rules.propagation.store;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * this class stores the rules catalog.
 * 
 * @author tmessini
 */
public class NodeRuleStore {

    /***/
    @SuppressWarnings("unused")
    private static final Logger                      log         = LoggerFactory.getLogger(NodeRuleStore.class);
    /***/
    private long                                     lastChanged = 0;
    /***/
    private ConcurrentHashMap<Integer, String> rulesNum;


    /**
     * 
     */
    public NodeRuleStore() {
        rulesNum = new ConcurrentHashMap<Integer, String>();
    }


    /**
     * stores the rule.
     * 
     * @param rule
     * @param randomID
     */
    public void addRule(final String rule, final Integer randomID) {
        rulesNum.put(randomID, rule);
        setLastChanged(Long.valueOf(System.currentTimeMillis()));
    }


    /**
     * return if the rule is there or not
     * 
     * @param rule
     * @return true/false
     */
    public boolean containsRule(final String rule) {
        return rulesNum.containsValue(rule);
    }


    /**
     * removes the rule.
     * 
     * @param ruleId
     */
    public void deleteRule(final Integer ruleId) {
        rulesNum.remove(ruleId);
        setLastChanged(Long.valueOf(System.currentTimeMillis()));
    }


    /**
     * returns the difference timestamp of current with the previous change
     * 
     * @return along number of last change
     */
    public long getLastChangedDiff() {
        return System.currentTimeMillis() - lastChanged;
    }


    /**
     * returns the String of rules.
     * 
     * @param id
     * @return String
     */
    public String getRule(final Integer id) {
        return rulesNum.get(id);
    }


    /**
     * returns all values as string
     * 
     * @return string
     */
    public String getRules() {
        return rulesNum.toString();
    }


    /**
     * returns the size of the store.
     * 
     * @return int
     */
    public int getRulesCatalogSize() {
        return rulesNum.size();
    }


    /**
     * @return the map of rules
     */
    public ConcurrentHashMap<Integer, String> getRulesMap() {
        return rulesNum;
    }
    

    /**
     * @param map
     */
    public  void setRulesMap(ConcurrentHashMap<Integer, String> map ) {
        rulesNum=map;
    }


    /**
     * changes the lastChanged
     * 
     * @param lastChanged
     */
    public void setLastChanged(final long lastChanged) {
        this.lastChanged = lastChanged;
    }

}
