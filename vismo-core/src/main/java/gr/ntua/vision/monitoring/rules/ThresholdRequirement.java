package gr.ntua.vision.monitoring.rules;

import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.predicateFrom;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.requireNotNull;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRequirementBean;
import gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.ThresholdPredicate;


/**
 * 
 */
public class ThresholdRequirement {
    /***/
    private final String             metric;
    /***/
    private final ThresholdPredicate pred;
    /***/
    private final double             thresholdValue;


    /**
     * Constructor.
     * 
     * @param bean
     */
    private ThresholdRequirement(final ThresholdRequirementBean bean) {
        this.thresholdValue = bean.getThreshold();
        this.pred = predicateFrom(bean.getPredicate());
        this.metric = requireNotNull(bean.getMetric());
    }


    /**
     * @param e
     * @return
     */
    public boolean isApplicable(final MonitoringEvent e) {
        return e.get(metric) != null;
    }


    /**
     * @param e
     * @return
     */
    public Violation isViolated(final MonitoringEvent e) {
        final double observedValue = (Double) e.get(metric);
        final boolean res = pred.perform(observedValue, thresholdValue);

        return res ? new Violation(metric, thresholdValue, observedValue) : null;
    }


    /**
     * @param bean
     * @return the {@link ThresholdRequirement}.
     */
    public static ThresholdRequirement from(final ThresholdRequirementBean bean) {
        return new ThresholdRequirement(bean);
    }
}
