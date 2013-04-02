package gr.ntua.vision.monitoring.rules;

import static gr.ntua.vision.monitoring.rules.ThresholdRulesUtils.fromString;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesUtils.isApplicable;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesUtils.requireNotNull;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.rules.ThresholdRulesUtils.ThresholdPredicate;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class ThresholdPeriodicRule extends PeriodicRule {
    /***/
    private static final Logger      log  = LoggerFactory.getLogger(ThresholdPeriodicRule.class);
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
    public void performWith(final MonitoringEvent e) {
        log.trace("got event: {}", e);

        if (!isApplicable(e, metric, operation, aggregationUnit))
            collect(e);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.PeriodicRule#aggregate(java.util.List, long, long)
     */
    @Override
    protected MonitoringEvent aggregate(final List<MonitoringEvent> eventsList, final long tStart, final long tEnd) {
        /*final double eventValue = (Double) e.get(metric);

        if (thresholdExceededBy(eventValue)) {
            log.debug("have violation on metric '{}', offending value {}", metric, eventValue);
            send(new ThresholdEvent(uuid, e.originatingService(), topic, eventValue));
        }*/

        return null;
    }
}
