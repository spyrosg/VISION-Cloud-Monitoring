package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRuleValidationError;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class ThresholdRule extends Rule {
    /***/
    private enum ThresholdPredicate {
        /***/
        GE(">=") {
            @Override
            public boolean perform(final double x, final double y) {
                return x >= y;
            }
        },
        /***/
        GT(">") {
            @Override
            public boolean perform(final double x, final double y) {
                return x > y;
            }
        },
        /***/
        LE("<=") {
            @Override
            public boolean perform(final double x, final double y) {
                return x <= y;
            }
        },
        /***/
        LT("<") {
            @Override
            public boolean perform(final double x, final double y) {
                return x < y;
            }
        };

        /***/
        public final String op;


        /**
         * Constructor.
         * 
         * @param op
         */
        private ThresholdPredicate(final String op) {
            this.op = op;
        }


        /**
         * @param x
         * @param y
         * @return
         */
        public abstract boolean perform(final double x, final double y);
    }

    /** the log target. */
    private static final Logger      log  = LoggerFactory.getLogger(Rule.class);
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
    private final String             uuid = UUID.randomUUID().toString();


    /**
     * Constructor.
     * 
     * @param engine
     * @param topic
     * @param pred
     * @param operation
     * @param metric
     * @param thresholdValue
     */
    public ThresholdRule(final VismoRulesEngine engine, final String topic, final ThresholdPredicate pred,
            final String operation, final String metric, final double thresholdValue) {
        super(engine);
        this.topic = topic;
        this.pred = pred;
        this.operation = operation;
        this.metric = metric;
        this.thresholdValue = thresholdValue;
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
        log.debug("got event: {}", e);

        if (!isApplicable(e))
            return;

        final double eventValue = (Double) e.get(metric);

        if (thresholdExceededWith(eventValue)) {
            log.debug("have violation on metric '{}', offending value {}", metric, eventValue);
            send(new ThresholdEvent(uuid, e.originatingService(), topic, eventValue));
        }
    }


    /**
     * @param e
     * @return
     */
    private boolean isApplicable(final MonitoringEvent e) {
        if (operation != null) {
            if (operation.equals(e.get("operation")))
                return true;
            else
                return false;
        } else
            return false;
    }


    /**
     * @param eventValue
     * @return
     */
    private boolean thresholdExceededWith(final double eventValue) {
        return pred.perform(eventValue, thresholdValue);
    }


    /**
     * @param op
     * @return
     * @throws ThresholdRuleValidationError
     */
    public static ThresholdPredicate predicateFromString(final String op) throws ThresholdRuleValidationError {
        for (final ThresholdPredicate p : ThresholdPredicate.values())
            if (p.op.equals(op))
                return p;

        throw new ThresholdRuleValidationError("unsupported predicate: " + op);
    }
}
