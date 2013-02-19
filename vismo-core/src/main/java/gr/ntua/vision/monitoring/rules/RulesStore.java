package gr.ntua.vision.monitoring.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * This is used to maintain the rules in the system.
 */
public class RulesStore {
    /** the rule-set. */
    private final Set<VismoRule> set;


    /**
     * Constructor.
     */
    public RulesStore() {
        this(new HashSet<VismoRule>());
    }


    /**
     * Constructor.
     * 
     * @param set
     *            the rule set.
     */
    public RulesStore(final Set<VismoRule> set) {
        this.set = set;
    }


    /**
     * Add a rule to the store.
     * 
     * @param rule
     *            the rule.
     */
    public void add(final VismoRule rule) {
        set.add(rule);
    }


    /**
     * Remove all rules.
     */
    public void clear() {
        set.clear();
    }


    /**
     * Check that the rule is member of the store.
     * 
     * @param rule
     *            the rule.
     * @return <code>true</code> iff the rule is in the store, <code>false</code> otherwise.
     */
    public boolean contains(final VismoRule rule) {
        return set.contains(rule);
    }


    /**
     * For each rule stored, run the given operation.
     * 
     * @param op
     *            the operation.
     */
    public void forEach(final RuleOperation op) {
        for (final VismoRule rule : new ArrayList<VismoRule>(set))
            op.run(rule);
    }


    /**
     * Remove the specified rule from the store.
     * 
     * @param rule
     *            the rule.
     */
    public void remove(final VismoRule rule) {
        set.remove(rule);
    }


    /**
     * @return the number of rules stored.
     */
    public int size() {
        return set.size();
    }
}
