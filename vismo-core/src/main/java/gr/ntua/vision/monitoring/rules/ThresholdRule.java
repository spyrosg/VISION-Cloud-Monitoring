package gr.ntua.vision.monitoring.rules;

import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.isApplicable;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.predicateFrom;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.requireNotNull;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.thresholdExceededBy;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.ThresholdPredicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class ThresholdRule extends Rule {
    /** the log target. */
    private static final Logger      log = LoggerFactory.getLogger(Rule.class);
    /***/
    private final String             filterUnit;
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
    public ThresholdRule(final VismoRulesEngine engine, final ThresholdRuleBean bean) {
        super(engine);
        this.topic = requireNotNull(bean.getTopic());
        this.pred = predicateFrom(bean.getPredicate());
        this.operation = bean.getOperation();
        this.metric = requireNotNull(bean.getMetric());
        this.filterUnit = bean.getFilterUnit();
        this.thresholdValue = bean.getThreshold();
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
     */
    @Override
    public void performWith(final MonitoringEvent e) {
        if (!isApplicable(e, metric, operation, filterUnit))
            return;

        log.debug("got applicable: {}", e);

        final double eventValue = (Double) e.get(metric);

        if (thresholdExceededBy(pred, eventValue, thresholdValue)) {
            log.debug("have violation of metric '{}', offending value {}", metric, eventValue);
            send(new ThresholdEvent(id(), e.originatingService(), topic, eventValue));
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<ThresholdRule: " + topic + ", " + metric + " " + pred.name + " " + thresholdValue + ">";
    }
}
