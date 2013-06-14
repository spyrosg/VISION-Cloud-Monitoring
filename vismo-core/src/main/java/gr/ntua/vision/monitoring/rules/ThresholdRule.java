package gr.ntua.vision.monitoring.rules;

import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.isApplicable;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.requireNotNull;
import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.thresholdExceededBy;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class ThresholdRule extends Rule {
    /** the log target. */
    private static final Logger            log = LoggerFactory.getLogger(Rule.class);
    /***/
    private final String                   filterUnit;
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
        super(engine);
        this.topic = requireNotNull(bean.getTopic());
        this.operation = bean.getOperation();
        this.filterUnit = bean.getFilterUnit();
        this.requirements = ThresholdRequirementList.from(bean.getRequirements());
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
     */
    @Override
    public void performWith(final MonitoringEvent e) {
        if (!isApplicable(e, filterUnit, operation, requirements))
            return;

        log.debug("got applicable: {}", e);

        final ViolationsList violations = thresholdExceededBy(e, requirements);

        if (violations.size() > 0) {
            log.debug("have: {}", violations);
            send(new ThresholdEvent(id(), e.originatingService(), topic, violations));
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<ThresholdRule: " + topic + ", on " + filterUnit + " with " + requirements.size() + " requirements>";
    }
}
