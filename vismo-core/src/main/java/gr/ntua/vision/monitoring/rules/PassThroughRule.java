package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 * 
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
     * @see gr.ntua.vision.monitoring.rules.RuleProc#id()
     */
    @Override
    public String id() {
        return toString();
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
     */
    @Override
    public void performWith(final MonitoringEvent e) {
        send(e);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<PassThroughRule>";
    }
}
