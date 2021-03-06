package gr.ntua.vision.monitoring.rules;

import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.isApplicable;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class ThresholdRule extends Rule {
    /** the log target. */
    private static final Logger            log         = LoggerFactory.getLogger(Rule.class);
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
    public ThresholdRule(final VismoRulesEngine engine, final ThresholdRuleBean bean) {
        super(engine, bean.getId());
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

        final ViolationsList violations = thresholdExceededBy(e);

        if (violations.size() > 0) {
            log.debug("have: {}", violations);
            send(ThresholdEventFactory.newEvent(id(), topic, e, violations));
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<ThresholdRule: " + topic + ", on " + filterUnits + " with " + requirements.size() + " requirements>";
    }


    /**
     * @param filterUnit
     */
    public void updateFilterUnits(final String filterUnit) {
        filterUnits.add(filterUnit);
    }


    /**
     * @param e
     * @return the violations list.
     */
    private ViolationsList thresholdExceededBy(final MonitoringEvent e) {
        return requirements.haveViolations(e, filterUnits);
    }
}
