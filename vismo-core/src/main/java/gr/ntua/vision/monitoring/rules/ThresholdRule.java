package gr.ntua.vision.monitoring.rules;

import static gr.ntua.vision.monitoring.rules.ThresholdRulesUtils.fromString;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesUtils.isApplicable;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesUtils.requireNotNull;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.rules.ThresholdRulesUtils.ThresholdPredicate;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class ThresholdRule extends Rule {
    /** the log target. */
    private static final Logger      log  = LoggerFactory.getLogger(Rule.class);
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
    public ThresholdRule(final VismoRulesEngine engine, final ThresholdRuleBean bean) {
        super(engine);
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
            return;

        final double eventValue = (Double) e.get(metric);

        if (thresholdExceededBy(eventValue)) {
            log.debug("have violation on metric '{}', offending value {}", metric, eventValue);
            send(new ThresholdEvent(uuid, e.originatingService(), topic, eventValue));
        }
    }


    /**
     * @param eventValue
     * @return <code>true</code> when <code>eventValue</code> has exceeded <code>thresholdValue</code>.
     */
    private boolean thresholdExceededBy(final double eventValue) {
        return pred.perform(eventValue, thresholdValue);
    }
}
