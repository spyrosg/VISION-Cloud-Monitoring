package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;


/**
 * 
 */
public abstract class Rule implements RuleProc<Event> {
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
     * @see gr.ntua.vision.monitoring.rules.RuleProc#submitTo(gr.ntua.vision.monitoring.rules.VismoRulesEngine)
     */
    @Override
    public void submitTo(final VismoRulesEngine eng) {
        eng.submitRule(this);
    }


    /**
     * @param e
     */
    protected void send(final Event e) {
        engine.send(e);
    }
}
