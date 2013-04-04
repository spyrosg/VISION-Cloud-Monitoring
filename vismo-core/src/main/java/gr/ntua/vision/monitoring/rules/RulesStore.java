package gr.ntua.vision.monitoring.rules;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to maintain the rules in the system.
 */
public class RulesStore {
    /***/
    private static final Logger          log = LoggerFactory.getLogger(RulesStore.class);

    /** the rule-set (mapping rule ids to rules). */
    private final Map<String, VismoRule> map;


    /**
     * Constructor.
     */
    public RulesStore() {
        this(new LinkedHashMap<String, VismoRule>());
    }


    /**
     * Constructor.
     * 
     * @param map
     *            the rule set.
     */
    public RulesStore(final Map<String, VismoRule> map) {
        this.map = map;
    }


    /**
     * Add a rule to the store.
     * 
     * @param rule
     *            the rule.
     */
    public void add(final VismoRule rule) {
        log.debug("adding rule: {}", rule.id());
        map.put(rule.id(), rule);
    }


    /**
     * Remove all rules.
     */
    public void clear() {
        map.clear();
    }


    /**
     * Check that the rule is member of the store.
     * 
     * @param rule
     *            the rule.
     * @return <code>true</code> iff the rule is in the store, <code>false</code> otherwise.
     */
    public boolean contains(final VismoRule rule) {
        return map.containsValue(rule);
    }


    /**
     * Check that there exists in the set a rule with the provided <code>id</code>.
     * 
     * @param id
     *            the id.
     * @return <code>true</code> iff a rule with given id exists in the set, <code>false</code> otherwise.
     */
    public boolean containsById(final String id) {
        return map.containsKey(id);
    }


    /**
     * For each rule stored, run the given operation.
     * 
     * @param op
     *            the operation.
     */
    public void forEach(final RuleOperation op) {
        for (final VismoRule rule : new ArrayList<VismoRule>(map.values()))
            op.run(rule);
    }


    /**
     * Remove rule by its id.
     * 
     * @param id
     *            the rule id.
     * @return <code>true</code> if the rule existed in the rule and has been removed, <code>false</code> otherwise.
     */
    public boolean remove(final String id) {
        log.debug("removing rule: {}", id);
        return map.remove(id) != null;
    }


    /**
     * Remove the specified rule from the store.
     * 
     * @param rule
     *            the rule.
     */
    public void remove(final VismoRule rule) {
        remove(rule.id());
    }


    /**
     * @return the number of rules stored.
     */
    public int size() {
        return map.size();
    }
}
