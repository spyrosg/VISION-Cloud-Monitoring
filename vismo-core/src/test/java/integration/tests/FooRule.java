package integration.tests;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.rules.Rule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;


/***/
public class FooRule extends Rule {
    /**
     * Constructor.
     * 
     * @param engine
     * @param id
     */
    public FooRule(final VismoRulesEngine engine, @SuppressWarnings("unused") final String id) {
        super(engine);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
     */
    @Override
    public void performWith(@SuppressWarnings("unused") final MonitoringEvent c) {
        // ignored
    }
}
