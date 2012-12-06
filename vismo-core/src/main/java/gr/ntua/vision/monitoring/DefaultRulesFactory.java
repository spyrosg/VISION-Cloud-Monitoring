package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;


/**
 *
 */
public abstract class DefaultRulesFactory extends VismoServiceAbstractFactory {
    /**
     * Submitting to rule engine the default rules.
     * 
     * @param engine
     *            the rules engine.
     */
    protected static void registerDefaultRules(final VismoRulesEngine engine) {
        new PassThroughRule(engine).submitTo(engine);
        // TODO: add SLAPerRequest rule
    }
}
