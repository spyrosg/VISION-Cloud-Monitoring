package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.resources.RuleBean;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.resources.ThresholdRuleValidationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to abstract away the creation of threshold rules; rules
 */
public class ThresholdRulesFactory extends AbstractRulesFactory {
    /** the log target. */
    private static final Logger log = LoggerFactory.getLogger(ThresholdRulesFactory.class);


    /**
     * Constructor.
     * 
     * @param next
     * @param engine
     */
    public ThresholdRulesFactory(final RulesFactory next, final VismoRulesEngine engine) {
        super(next, engine);
    }


    /**
     * Constructor.
     * 
     * @param engine
     */
    public ThresholdRulesFactory(final VismoRulesEngine engine) {
        super(engine);
    }


    /**
     * Validate bean; construct and return a new rule.
     * 
     * @param bean
     * @return on success, either a {@link PeriodicRule} or a {@link Rule}.
     * @throws ThresholdRuleValidationError
     *             when a required parameter (field) is missing or has an invalid value.
     */
    @Override
    public VismoRule buildFrom(final RuleBean bean) {
        if (!(bean instanceof ThresholdRuleBean)) {
            log.debug("bean {} not applicable; trying next", bean);

            return next().buildFrom(bean);
        }

        return ThresholdRulesTraits.build(engine, (ThresholdRuleBean) bean);
    }
}
