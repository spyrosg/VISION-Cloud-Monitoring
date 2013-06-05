package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 * This rule is used to pass the events "as-is".
 */
public class PassThroughRule extends Rule {
    /**
     * Constructor.
     * 
     * @param engine
     */
    public PassThroughRule(final VismoRulesEngine engine) {
        super(engine);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
     */
    @Override
    public void performWith(final MonitoringEvent e) {
        send(e);
    }
}
