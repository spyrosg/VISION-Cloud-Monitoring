package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;


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
    public RuleProc<Event> createRule(VismoRulesEngine vismoRulesEngine, String period, String name, String desc);
}
