package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


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
    public RuleProc<MonitoringEvent> createRule(VismoRulesEngine vismoRulesEngine, String period, String name, String desc);
}
