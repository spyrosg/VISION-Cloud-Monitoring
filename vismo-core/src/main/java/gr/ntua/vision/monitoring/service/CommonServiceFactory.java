package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.threading.JVMStatusReportTask;


/**
 * This is used to factor out common functionality for all services.
 */
public abstract class CommonServiceFactory extends VismoServiceAbstractFactory {
    /**
     * @see gr.ntua.vision.monitoring.service.VismoServiceAbstractFactory#bootstrap(gr.ntua.vision.monitoring.service.VismoService)
     */
    @Override
    protected void bootstrap(final VismoService service) {
        service.addTask(new JVMStatusReportTask(60 * 1000));
    }


    /**
     * Submitting default rules to the rules' engine.
     * 
     * @param engine
     *            the rules engine.
     */
    protected static void registerDefaultRules(final VismoRulesEngine engine) {
        new PassThroughRule(engine).submitTo(engine);
        // TODO: add SLAPerRequest rule
    }
}
