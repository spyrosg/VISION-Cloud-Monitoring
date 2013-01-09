package gr.ntua.vision.monitoring.rules;

/**
 * @author tmessini
 */
public interface RuleFactory {
    /**
     * @param vismoRulesEngine
     * @param period
     * @param name
     * @param desc
     * @return rule
     */
    public Object createRule(VismoRulesEngine vismoRulesEngine, String period, String name, String desc);
}
