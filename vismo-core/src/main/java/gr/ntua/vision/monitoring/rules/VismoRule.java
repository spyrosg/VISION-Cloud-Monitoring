package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;


/**
 * 
 */
public abstract class VismoRule implements RuleProc<Event> {
    /***/
    private final VismoRulesEngine engine;


    /**
     * Constructor.
     * 
     * @param engine
     */
    public VismoRule(final VismoRulesEngine engine) {
        this.engine = engine;
    }


    /**
     * @param e
     */
    protected void send(final Event e) {
        engine.send(e);
    }
}
