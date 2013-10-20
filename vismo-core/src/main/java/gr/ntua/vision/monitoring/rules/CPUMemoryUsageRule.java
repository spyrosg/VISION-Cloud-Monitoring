package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.List;


/**
 *
 */
public class CPUMemoryUsageRule extends PeriodicRule {
    /**
     * @param engine
     * @param period
     */
    public CPUMemoryUsageRule(final VismoRulesEngine engine, final long period) {
        super(engine, period);
    }

    /**
     * @param engine
     * @param period
     * @param id
     */
    public CPUMemoryUsageRule(final VismoRulesEngine engine, final long period, final String id) {
        super(engine, period, id);
    



    
    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
     */
    @Override
    public void performWith(final MonitoringEvent c) {
        // TODO Auto-generated method stub

    }

    /**
     * @see gr.ntua.vision.monitoring.rules.PeriodicRule#aggregate(java.util.List, long, long)
     */
    @Override
    protected MonitoringEvent aggregate(final List<MonitoringEvent> list, final long tStart, final long tEnd) {
        // TODO Auto-generated method stub
        return null;
    }    private static double hostCPULoad() {
        
    }


    private static double processCPULoad() {
        return 0;
    }

}
