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
    private final ConcurrentHashMap<String, Integer> rulesCatalog;
    /***/
    private final ConcurrentHashMap<Integer, String> rulesNum;


    /**
     * 
     */
    public RuleStore() {
        rulesCatalog = new ConcurrentHashMap<String, Integer>();
        rulesNum = new ConcurrentHashMap<Integer, String>();
    }


    /**
     * stores the rule.
     * 
     * @param rule
     * @param randomID
     */
    public void addRule(final String rule, final int randomID) {
        rulesCatalog.put(rule, randomID);
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
     * @param rule
     */
    public void deleteRule(final String rule) {
        rulesNum.remove(rulesCatalog.get(rule));
        rulesCatalog.remove(rule);
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RuleStore other = (RuleStore) obj;
        if (rulesCatalog == null) {
            if (other.rulesCatalog != null)
                return false;
        } else if (!rulesCatalog.equals(other.rulesCatalog))
            return false;
        if (rulesNum == null) {
            if (other.rulesNum != null)
                return false;
        } else if (!rulesNum.equals(other.rulesNum))
            return false;
        return true;
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
        return rulesCatalog.toString();
    }


    /**
     * returns the size of the store.
     * 
     * @return int
     */
    public int getRulesCatalogSize() {
        return rulesCatalog.size();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rulesCatalog == null) ? 0 : rulesCatalog.hashCode());
        result = prime * result + ((rulesNum == null) ? 0 : rulesNum.hashCode());
        return result;
    }

}
