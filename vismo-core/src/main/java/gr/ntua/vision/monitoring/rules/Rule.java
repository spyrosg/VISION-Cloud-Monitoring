package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.UUID;


/**
 * This is the base class for all rules that operate on an event basis (in contrast to {@link PeriodicRule}s that work on a set of
 * events).
 */
public abstract class Rule implements VismoRule {
    /***/
    private final VismoRulesEngine engine;
    /***/
    private final String           id;


    /**
     * Constructor.
     * 
     * @param engine
     */
    public Rule(final VismoRulesEngine engine) {
        this.engine = engine;
        this.id = UUID.randomUUID().toString();
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#id()
     */
    @Override
    public String id() {
        return id;
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#submit()
     */
    @Override
    public void submit() {
        engine.submitRule(this);
    }


    /**
     * @param e
     */
    protected void send(final MonitoringEvent e) {
        engine.send(e);
    }
}
