package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;


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
     * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
     */
    @Override
    public void performWith(final Event e) {
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
