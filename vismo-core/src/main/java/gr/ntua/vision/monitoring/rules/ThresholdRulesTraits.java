package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRuleValidationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
class ThresholdRulesTraits {
    /***/
    public enum ThresholdPredicate {
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
         * @return the evaluation result of the operation.
         */
        public abstract boolean perform(final double x, final double y);
    }

    /***/
    private static final Logger   log   = LoggerFactory.getLogger(ThresholdRulesTraits.class);

    /***/
    private static final String[] UNITS = { "tenant", "user", "container", "object" };


    /**
     * Constructor.
     */
    private ThresholdRulesTraits() {
    }


    /**
     * When <code>aggregationUnit</code> is unspecified by the user, this method returns <code>true</code>, since it means that it
     * applies to all units.
     * 
     * @param e
     * @param aggregationUnit
     * @return <code>true</code> if the event comes from a matching unit.
     */
    static boolean checkAggregationUnit(final MonitoringEvent e, final String aggregationUnit) {
        if (aggregationUnit == null || aggregationUnit.isEmpty())
            return true;

        final String[] fs = aggregationUnit.split(",");

        for (int i = 0; i < UNITS.length; ++i) {
            if (i >= fs.length)
                continue;

            final String val = fs[i];
            final String unit = UNITS[i];
            final Object o = e.get(unit);

            if (o == null)
                continue;

            log.trace(String.format("unit %s => %s matching %s", unit, o, val));

            if (!o.equals(val))
                return false;
        }

        return true;
    }


    /**
     * When <code>operation</code> is unspecified by the user, this method returns <code>true</code>, since it means that it
     * applies to all operations.
     * 
     * @param e
     * @param operation
     * @return <code>true</code> if the event comes from a matching operation.
     */
    static boolean checkOperation(final MonitoringEvent e, final String operation) {
        if (operation == null || operation.isEmpty())
            return true;

        if (operation.equals(e.get("operation")))
            return true;

        return false;
    }


    /**
     * @param op
     * @return a {@link ThresholdPredicate}.
     * @throws ThresholdRuleValidationError
     */
    static ThresholdPredicate fromString(final String op) throws ThresholdRuleValidationError {
        for (final ThresholdPredicate p : ThresholdPredicate.values())
            if (p.op.equals(op))
                return p;

        throw new ThresholdRuleValidationError("unsupported predicate: " + op);
    }


    /**
     * @param e
     * @param metric
     * @param operation
     * @param aggregationUnit
     * @return <code>true</code> if this is an event that matches <code>this</code> rule.
     */
    static boolean isApplicable(final MonitoringEvent e, final String metric, final String operation, final String aggregationUnit) {
        return e.get(metric) != null && checkOperation(e, operation) && checkAggregationUnit(e, aggregationUnit);
    }


    /**
     * @param s
     * @return <code>s</code> if the string can be considered non empty.
     * @throws ThresholdRuleValidationError
     *             when the string is empty.
     */
    static String requireNotNull(final String s) {
        if (s == null || s.isEmpty())
            throw new ThresholdRuleValidationError("empty");

        return s;
    }
}
