package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRequirementBean;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.resources.ThresholdRuleValidationError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
class ThresholdRulesTraits {
    /** the list of fields that should have a value in the provided beans. */
    private static final String[] beanFields        = { "topic", "requirements" };
    /***/
    private static final Logger   log               = LoggerFactory.getLogger(ThresholdRulesTraits.class);
    /***/
    private static final String[] requirementFields = { "predicate", "threshold" };


    /**
     * Constructor.
     */
    private ThresholdRulesTraits() {
    }


    /**
     * Construct and return a rule, based on the bean input.
     * 
     * @param engine
     * @param bean
     * @return a thresold rule object.
     */
    public static VismoRule build(final VismoRulesEngine engine, final ThresholdRuleBean bean) {
        validate(bean);

        if (bean.getPeriod() > 0)
            return new ThresholdPeriodicRule(engine, bean);

        return new ThresholdRule(engine, bean);
    }


    /**
     * @param e
     * @param filterUnit
     * @param operation
     * @param list
     * @return <code>true</code> iff the monitoring event matches all of the requirements, <code>false</code> otherwise.
     */
    static boolean isApplicable(final MonitoringEvent e, final List<String> filterUnit, final String operation,
            final ThresholdRequirementList list) {
        return isApplicableOperation(e, operation) && isApplicableFilterUnit(e, filterUnit) && list.isApplicable(e);
    }


    /**
     * When <code>filterUnit</code> is unspecified by the user, this method returns <code>true</code>, since it means that it
     * applies to all units.
     * 
     * @param e
     * @param filterUnits
     * @return <code>true</code> if the event comes from a matching unit.
     */
    static boolean isApplicableFilterUnit(final MonitoringEvent e, final List<String> filterUnits) {
        if (filterUnits.size() == 0)
            return true;

        final String concat = join(e);

        log.debug("matching /{}/ against filter: {}", concat, filterUnits);

        if (concat.length() == 0)
            return true;

        for (final String filterUnit : filterUnits)
            if (concat.startsWith(filterUnit))
                return true;

        return false;
    }


    /**
     * @param e
     * @return @see {@link #join(String...)}
     */
    static String join(final MonitoringEvent e) {
        final String tenant = (String) e.get("tenant");
        final String user = (String) e.get("user");
        final String container = (String) e.get("container");
        final String object = (String) e.get("object");

        return join(tenant, user, container, object);
    }


    /**
     * When <code>operation</code> is unspecified by the user, this method returns <code>true</code>, since it means that it
     * applies to all operations.
     * 
     * @param e
     * @param operation
     * @return <code>true</code> if the event comes from a matching operation.
     */
    private static boolean isApplicableOperation(final MonitoringEvent e, final String operation) {
        if (operation == null || operation.isEmpty())
            return true;

        return operation.equals(e.get("operation"));
    }


    /**
     * @param fs
     * @return the comma separated string concatenation of the strings.
     */
    private static String join(final String... fs) {
        final StringBuilder buf = new StringBuilder();

        for (int i = 0; i < fs.length; ++i) {
            final String s = fs[i];

            if (s == null)
                return buf.toString();

            buf.append(s);

            if (i < fs.length - 1)
                buf.append(",");
        }

        return buf.toString();
    }


    /**
     * Check that all required fields have an actual value.
     * 
     * @param o
     * @param fields
     * @throws SecurityException
     */
    private static void validate(final Object o, final String[] fields) throws SecurityException {
        for (final String field : fields) {
            final String getterName = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);

            final Method m;

            try {
                m = o.getClass().getDeclaredMethod(getterName);
            } catch (final NoSuchMethodException e) {
                log.warn("validating field " + field + " failed; ignoring", e);
                continue;
            }

            try {
                if (m.invoke(o) == null) // no value provided
                    throw new ThresholdRuleValidationError("invalid rule specification: '" + field + "' field is required");
            } catch (final IllegalAccessException e) {
                log.warn("validating field " + field + " failed; ignoring", e);
            } catch (final IllegalArgumentException e) {
                log.warn("validating field " + field + " failed; ignoring", e);
            } catch (final InvocationTargetException e) {
                log.warn("validating field " + field + " failed; ignoring", e);
            }
        }
    }


    /**
     * @param bean
     */
    private static void validate(final ThresholdRuleBean bean) {
        validate(bean, beanFields);

        for (final ThresholdRequirementBean rbean : bean.getRequirements())
            validate(rbean, requirementFields);
    }
}
