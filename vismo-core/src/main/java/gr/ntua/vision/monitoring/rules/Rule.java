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
        this(engine, getId());
    }


    /**
     * Constructor.
     * 
     * @param engine
     * @param id
     */
    public Rule(final VismoRulesEngine engine, final String id) {
        this.engine = engine;
        this.id = id != null ? id : getId();
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<" + getClass().getSimpleName() + ">";
    }


    /**
     * @param e
     */
    protected void send(final MonitoringEvent e) {
        engine.send(e);
    }


    /**
     * @return a uuid for this rule.
     */
    private static String getId() {
        return UUID.randomUUID().toString();
    }
}
