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
    private static final Logger                      log = LoggerFactory.getLogger(RulesManagementResource.class);
    /***/
    private final ConcurrentHashMap<Integer, String> rulesNum;


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

}
