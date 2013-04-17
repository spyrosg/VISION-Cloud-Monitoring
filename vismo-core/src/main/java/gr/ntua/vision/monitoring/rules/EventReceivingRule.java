package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 * 
 */
public class EventReceivingRule extends Rule {
    /**
     * Constructor.
     * 
     * @param engine
     */
    public EventReceivingRule(final VismoRulesEngine engine) {
        super(engine);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
     */
    @Override
    public void performWith(final MonitoringEvent e) {
        System.out.println("id=" + e.get("id") + ", core=" + System.currentTimeMillis() + ", ts=" + e.timestamp());
    }
}
