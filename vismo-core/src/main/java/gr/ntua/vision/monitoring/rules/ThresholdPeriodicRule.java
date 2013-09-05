package gr.ntua.vision.monitoring.rules;

import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.isApplicable;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class ThresholdPeriodicRule extends PeriodicRule {
    /***/
    private static final Logger            log         = LoggerFactory.getLogger(ThresholdPeriodicRule.class);
    /***/
    private final ArrayList<String>        filterUnits = new ArrayList<String>();
    /***/
    private final String                   operation;
    /***/
    private final ThresholdRequirementList requirements;
    /***/
    private final String                   topic;


    /**
     * Constructor.
     * 
     * @param engine
     * @param bean
     */
    public ThresholdPeriodicRule(final VismoRulesEngine engine, final ThresholdRuleBean bean) {
        super(engine, bean.getPeriod());
        this.topic = bean.getTopic();
        this.operation = bean.getOperation();
        this.filterUnits.add(bean.getFilterUnit());
        this.requirements = ThresholdRequirementList.from(bean.getRequirements());
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
     */
    @Override
    public void performWith(final MonitoringEvent e) {
        if (!isApplicable(e, filterUnits, operation, requirements))
            return;

        log.debug("got applicable: {}", e);
        collect(e);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<ThresholdPeriodicRule: " + topic + ", period=" + (period() / 1000.0) + "s, on " + filterUnits + " with "
                + requirements.size() + " requirements>";
    }


    /**
     * @param filterUnit
     */
    public void updateFilterUnits(final String filterUnit) {
        filterUnits.add(filterUnit);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.PeriodicRule#aggregate(java.util.List, long, long)
     */
    @Override
    protected MonitoringEvent aggregate(final List<MonitoringEvent> list, final long tStart, final long tEnd) {
        final ViolationsList violations = thresholdExceededBy(list);

        if (violations.size() > 0) {
            log.debug("have: {}", violations);

            return ThresholdEventFactory.newEvent(id(), topic, list.get(0), violations);
        }

        return null;
    }


    /**
     * @param events
     * @return the violations list.
     */
    private ViolationsList thresholdExceededBy(final List<MonitoringEvent> events) {
        return requirements.haveViolations(events, filterUnits);
    }
}
