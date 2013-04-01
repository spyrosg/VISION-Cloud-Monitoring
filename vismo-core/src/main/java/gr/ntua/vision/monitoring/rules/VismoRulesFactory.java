package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.resources.ThresholdRuleValidationError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to abstract away the creation of threshold rules; rules
 */
public class VismoRulesFactory {
    /** the log target. */
    private static final Logger    log            = LoggerFactory.getLogger(VismoRulesFactory.class);
    /** the list of fields that should have a value in the provided beans. */
    private static final String[]  requiredFields = { "topic", "predicate", "threshold", "metric" };
    /** the rules engine. */
    private final VismoRulesEngine engine;


    /**
     * Constructor.
     * 
     * @param engine
     */
    public VismoRulesFactory(final VismoRulesEngine engine) {
        this.engine = engine;
    }


    /**
     * Validate bean; construct and return a new rule.
     * 
     * @param bean
     * @return on success, either a {@link PeriodicRule} or a {@link Rule}.
     * @throws ThresholdRuleValidationError
     *             when a required parameter (field) is missing or has an invalid value.
     */
    public VismoRule buildRule(final ThresholdRuleBean bean) throws ThresholdRuleValidationError {
        validateBean(bean);

        return buildFrom(bean);
    }


    /**
     * Construct a rule using the given bean.
     * 
     * @param bean
     * @return a {@link VismoRule} object.
     */
    private VismoRule buildFrom(final ThresholdRuleBean bean) {
        if (bean.getPeriod() > 0)
            return buildThresholdPeriodicRule(bean);

        return buildThresholdRule(bean);
    }


    /**
     * @param bean
     * @return a {@link ThresholdPeriodicRule}.
     */
    private ThresholdPeriodicRule buildThresholdPeriodicRule(final ThresholdRuleBean bean) {
        // TODO
        return null;
    }


    /**
     * @param bean
     * @return a {@link ThresholdRule}.
     */
    private ThresholdRule buildThresholdRule(final ThresholdRuleBean bean) {
        return new ThresholdRule(engine, bean);
    }


    /**
     * Check that all required fields have an actual value.
     * 
     * @param bean
     * @throws SecurityException
     */
    private static void validateBean(final ThresholdRuleBean bean) throws SecurityException {
        for (final String field : requiredFields) {
            final String getterName = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);

            final Method m;

            try {
                m = bean.getClass().getDeclaredMethod(getterName);
            } catch (final NoSuchMethodException e) {
                log.warn("validating field " + field + " failed; ignoring", e);
                continue;
            }

            try {
                if (m.invoke(bean) == null) // no value provided
                    throw new ThresholdRuleValidationError("'" + field + "' field is required");
            } catch (final IllegalAccessException e) {
                log.warn("validating field " + field + " failed; ignoring", e);
            } catch (final IllegalArgumentException e) {
                log.warn("validating field " + field + " failed; ignoring", e);
            } catch (final InvocationTargetException e) {
                log.warn("validating field " + field + " failed; ignoring", e);
            }
        }
    }
}
