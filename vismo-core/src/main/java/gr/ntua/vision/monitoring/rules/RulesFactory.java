package gr.ntua.vision.monitoring.rules;

/**
 * @author tmessini
 */
public interface RulesFactory {
    /**
     * @param vismoRulesEngine
     * @param period
     * @param name
     * @param desc
     * @return rule
     */
    public VismoRule createRule(VismoRulesEngine vismoRulesEngine, String period, String name, String desc);
}
