package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.List;


/**
 * 
 */
public class ThresholdPeriodicRule extends PeriodicRule {
    /***/
    private final String topic;


    /**
     * Constructor.
     * 
     * @param engine
     * @param period
     * @param topic
     */
    public ThresholdPeriodicRule(final VismoRulesEngine engine, final long period, final String topic) {
        super(engine, period);
        this.topic = topic;
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#id()
     */
    @Override
    public String id() {
        // TODO Auto-generated method stub
        return null;
    }


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
    protected MonitoringEvent aggregate(final List<MonitoringEvent> eventsList, final long tStart, final long tEnd) {
        // TODO Auto-generated method stub
        return null;
    }
}
