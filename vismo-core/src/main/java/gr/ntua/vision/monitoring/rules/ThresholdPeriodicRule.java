package gr.ntua.vision.monitoring.rules;

import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.foldFrom;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.isApplicable;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.predicateFrom;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.requireNotNull;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.thresholdExceededBy;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.ThresholdFold;
import gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.ThresholdPredicate;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class ThresholdPeriodicRule extends PeriodicRule {
    /***/
    private static final Logger      log = LoggerFactory.getLogger(ThresholdPeriodicRule.class);
    /***/
    private final String             aggregationUnit;
    /***/
    private final ThresholdFold      foldMethod;
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


    /**
     * Constructor.
     * 
     * @param engine
     * @param bean
     */
    public ThresholdPeriodicRule(final VismoRulesEngine engine, final ThresholdRuleBean bean) {
        super(engine, bean.getPeriod());
        this.topic = requireNotNull(bean.getTopic());
        this.pred = predicateFrom(bean.getPredicate());
        this.operation = bean.getOperation();
        this.metric = requireNotNull(bean.getMetric());
        this.aggregationUnit = bean.getAggregationUnit();
        this.thresholdValue = bean.getThreshold();
        this.foldMethod = foldFrom(bean.getAggregationMethod());
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
     */
    @Override
    public void performWith(final MonitoringEvent e) {
        if (isApplicable(e, metric, operation, aggregationUnit)) {
            log.debug("got applicable: {}", e);
            collect(e);
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<ThresholdPeriodicRule: " + id() + ", period: " + period() + ", topic: " + topic + ">";
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.PeriodicRule#aggregate(java.util.List, long, long)
     */
    @Override
    protected MonitoringEvent aggregate(final List<MonitoringEvent> eventsList, @SuppressWarnings("unused") final long tStart,
            @SuppressWarnings("unused") final long tEnd) {
        final double aggregatedValue = performFold(eventsList);

        if (!thresholdExceededBy(pred, aggregatedValue, thresholdValue))
            return null;

        log.debug(String.format("have violation of metric %s '%s', offending value %s", foldMethod, metric, aggregatedValue));

        return new ThresholdEvent(id(), eventsList.get(0).originatingService(), topic, aggregatedValue);
    }


    /**
     * @param eventsList
     * @return the fold application value.
     */
    private double performFold(final List<MonitoringEvent> eventsList) {
        final double arr[] = new double[eventsList.size()];

        for (int i = 0; i < arr.length; ++i)
            arr[i] = (Double) eventsList.get(i).get(metric);

        return foldMethod.perform(arr);
    }
}
