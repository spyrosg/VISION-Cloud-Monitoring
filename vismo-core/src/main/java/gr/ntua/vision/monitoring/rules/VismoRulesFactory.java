package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRuleBean;
import gr.ntua.vision.monitoring.resources.ThresholdRuleValidationError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

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
        return new Rule(engine) {
            /***/
            private static final String OBS  = "fake-obs";
            /** the log target. */
            private final Logger        log1 = LoggerFactory.getLogger(Rule.class);
            /***/
            private final String        uuid = UUID.randomUUID().toString();


            @Override
            public String id() {
                return uuid;
            }


            @Override
            public void performWith(final MonitoringEvent e) {
                if (!OBS.equals(e.originatingService()))
                    return;

                log1.debug("have event: {}", e);

                send(new MonitoringEvent() {
                    @Override
                    public Object get(final String key) {
                        return null;
                    }


                    @Override
                    public InetAddress originatingIP() throws UnknownHostException {
                        return e.originatingIP();
                    }


                    @Override
                    public String originatingService() {
                        return e.originatingService();
                    }


                    @Override
                    public long timestamp() {
                        return System.currentTimeMillis();
                    }


                    @Override
                    public String topic() {
                        return bean.getTopic();
                    }
                });
            }
        };
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
