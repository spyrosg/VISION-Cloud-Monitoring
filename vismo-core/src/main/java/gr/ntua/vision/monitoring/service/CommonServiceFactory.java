package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.threading.JVMStatusReportTask;


/**
 * This is used to factor out common functionality for all services.
 */
public abstract class CommonServiceFactory extends VismoServiceAbstractFactory {
    /**
     * Submitting default rules.
     * 
     * @see gr.ntua.vision.monitoring.service.VismoServiceAbstractFactory#boostrap(gr.ntua.vision.monitoring.rules.VismoRulesEngine)
     */
    @Override
    protected void boostrap(final VismoRulesEngine engine) {
        new PassThroughRule(engine).submitTo(engine);
        // TODO: add SLAPerRequest rule
    }


    /**
     * Scheduling default tasks.
     * 
     * @see gr.ntua.vision.monitoring.service.VismoServiceAbstractFactory#bootstrap(gr.ntua.vision.monitoring.service.VismoService)
     */
    @Override
    protected void bootstrap(final VismoService service) {
        final long ONE_MINUTE = 60 * 1000;

        service.addTask(new JVMStatusReportTask(ONE_MINUTE));
    }
}
