package gr.ntua.vision.monitoring.rules;

import static gr.ntua.vision.monitoring.rules.ThresholdRulesFactoryUtils.fromString;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesFactoryUtils.requireNotNull;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.rules.ThresholdRulesFactoryUtils.ThresholdPredicate;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class ThresholdRule extends Rule {

    /** the log target. */
    private static final Logger      log   = LoggerFactory.getLogger(Rule.class);
    /***/
    private static final String[]    UNITS = { "tenant", "user", "container", "object" };
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
    private final String             uuid  = UUID.randomUUID().toString();


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

        if (!isApplicable(e))
            return;

        final double eventValue = (Double) e.get(metric);

        if (thresholdExceededBy(eventValue)) {
            log.debug("have violation on metric '{}', offending value {}", metric, eventValue);
            send(new ThresholdEvent(uuid, e.originatingService(), topic, eventValue));
        }
    }


    /**
     * When <code>aggregationUnit</code> is unspecified by the user, this method returns <code>true</code>, since it means that it
     * applies to all units.
     * 
     * @param e
     * @return <code>true</code> if the event comes from a matching unit.
     */
    private boolean checkAggregationUnit(final MonitoringEvent e) {
        if (aggregationUnit == null || aggregationUnit.isEmpty())
            return true;

        final String[] fs = aggregationUnit.split(",");

        for (int i = 0; i < UNITS.length; ++i) {
            if (i >= fs.length)
                continue;

            final String val = fs[i];
            final String unit = UNITS[i];
            final Object o = e.get(unit);

            if (o == null)
                continue;

            log.trace(String.format("unit %s => %s matching %s", unit, o, val));

            if (!o.equals(val))
                return false;
        }

        return true;
    }


    /**
     * When <code>operation</code> is unspecified by the user, this method returns <code>true</code>, since it means that it
     * applies to all operations.
     * 
     * @param e
     * @return <code>true</code> if the event comes from a matching operation.
     */
    private boolean checkOperation(final MonitoringEvent e) {
        if (operation == null || operation.isEmpty())
            return true;

        if (operation.equals(e.get("operation")))
            return true;

        return false;
    }


    /**
     * @param e
     * @return <code>true</code> if this is an event that matches <code>this</code> rule.
     */
    private boolean isApplicable(final MonitoringEvent e) {
        return e.get(metric) != null && checkOperation(e) && checkAggregationUnit(e);

    }


    /**
     * @param eventValue
     * @return <code>true</code> when <code>eventValue</code> has exceeded <code>thresholdValue</code>.
     */
    private boolean thresholdExceededBy(final double eventValue) {
        return pred.perform(eventValue, thresholdValue);
    }
}
