package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;


/**
 * This is used to perform an operation over a rule.
 */
public interface RuleOperation {
    /**
     * Run the operation with given rule.
     * 
     * @param rule
     *            the rule.
     */
    void run(RuleProc<Event> rule);
}
