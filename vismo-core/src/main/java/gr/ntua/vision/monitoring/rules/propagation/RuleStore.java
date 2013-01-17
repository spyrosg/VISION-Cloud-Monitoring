package gr.ntua.vision.monitoring.rules.propagation;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * this class stores the rules catalog.
 * 
 * @author tmessini
 */
public class RuleStore {

    /***/
    @SuppressWarnings("unused")
    private static final Logger                      log = LoggerFactory.getLogger(RuleStore.class);
    /***/
    private final ConcurrentHashMap<Integer, String> rulesNum;
    /***/
    private long lastChanged=0;


    /**
     * 
     */
    public RuleStore() {
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
     * returns the String of rules.
     * 
     * @param id
     * @return String
     */
    public String getRule(final int id) {
        return rulesNum.get(id);
    }


    /**
     * returns all values as string
     * 
     * @return String
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
     * returns the difference timestamp of current 
     * with the previous change
     * @return along number of last change
     */
    public long getLastChangedDiff() {
        return System.currentTimeMillis()-lastChanged;
    }

    /**
     * changes the lastChanged
     * @param lastChanged
     */
    public void setLastChanged(long lastChanged) {
        this.lastChanged = lastChanged;
    }
    
}
