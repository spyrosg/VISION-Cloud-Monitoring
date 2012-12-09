package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;


/**
 *
 */
public abstract class CommonServiceFactory extends VismoServiceAbstractFactory {
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
