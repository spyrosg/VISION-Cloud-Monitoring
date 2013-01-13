package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 * 
 */
public abstract class Rule implements RuleProc<MonitoringEvent> {
    /***/
    private final VismoRulesEngine engine;


    /**
     * Constructor.
     * 
     * @param engine
     */
    public Rule(final VismoRulesEngine engine) {
        this.engine = engine;
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#submit()
     */
    @Override
    public void submit() {
        this.engine.submitRule(this);
    }


    /**
     * @param e
     */
    protected void send(final MonitoringEvent e) {
        engine.send(e);
    }
}
