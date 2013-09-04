package gr.ntua.vision.monitoring.rules;

import static gr.ntua.vision.monitoring.rules.ThresholdRulesTraits.isApplicable;

import java.util.List;

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
    // TODO: change filter unit to accept a list of containers.
    // TODO: update period. 
    // TODO: change #toString to show period.
    private final List<String>                   filterUnits;
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
        this.topic = bean.getTopic();
        this.operation = bean.getOperation();
        this.filterUnits = bean.getFilterUnits();
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
     * @param e
     * @return the violiations list.
     */
    private ViolationsList thresholdExceededBy(final MonitoringEvent e) {
        return requirements.haveViolations(e);
    }
}
