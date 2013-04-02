package gr.ntua.vision.monitoring.rules;

import static gr.ntua.vision.monitoring.rules.ThresholdRulesFactoryUtils.fromString;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesFactoryUtils.requireNotNull;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.rules.ThresholdRulesFactoryUtils.ThresholdPredicate;

import java.util.List;
import java.util.UUID;


/**
 * 
 */
public class ThresholdPeriodicRule extends PeriodicRule {
    /***/
    private final String             aggregationUnit;
    /***/
    private final String             metric;
    /***/
    private final String             operation;
    /***/
    private final ThresholdPredicate pred;
    /***/
    private final double             thresholdValue;
    /***/
    private final String             topic;
    /***/
    private final String             uuid = UUID.randomUUID().toString();


    /**
     * Constructor.
     * 
     * @param engine
     * @param bean
     */
    public ThresholdPeriodicRule(final VismoRulesEngine engine, final ThresholdRuleBean bean) {
        super(engine, bean.getPeriod());
        this.topic = requireNotNull(bean.getTopic());
        this.pred = fromString(bean.getPredicate());
        this.operation = bean.getOperation();
        this.metric = requireNotNull(bean.getMetric());
        this.aggregationUnit = bean.getAggregationUnit();
        this.thresholdValue = bean.getThreshold();
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#id()
     */
    @Override
    public String id() {
        return uuid;
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
