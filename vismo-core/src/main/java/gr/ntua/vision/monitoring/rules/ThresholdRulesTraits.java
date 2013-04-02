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
    public enum ThresholdFold {
        /***/
        AVG("avg") {
            @Override
            public double perform(final double[] arr) {
                if (arr.length == 0)
                    return 0;
                if (arr.length == 1)
                    return arr[0];

                return ThresholdFold.SUM.perform(arr) / arr.length;
            }
        },
        /***/
        MAX("max") {
            @Override
            public double perform(final double[] arr) {
                if (arr.length == 0)
                    return Double.POSITIVE_INFINITY;
                if (arr.length == 1)
                    return arr[0];

                double max = arr[0];

                for (int i = 0; i < arr.length; ++i)
                    if (arr[i] > max)
                        max = arr[i];

                return max;
            }
        },
        /***/
        MIN("min") {
            @Override
            public double perform(final double[] arr) {
                if (arr.length == 0)
                    return Double.NEGATIVE_INFINITY;
                if (arr.length == 1)
                    return arr[0];

                double min = arr[0];

                for (int i = 0; i < arr.length; ++i)
                    if (arr[i] < min)
                        min = arr[i];

                return min;
            }
        },
        /***/
        SUM("sum") {
            @Override
            public double perform(final double[] arr) {
                double sum = 0;

                for (int i = 0; i < arr.length; ++i)
                    sum += arr[i];

                return sum;
            }
        };

        /***/
        public final String name;


        /**
         * Constructor.
         * 
         * @param name
         */
        private ThresholdFold(final String name) {
            this.name = name;
        }


        /**
         * @param arr
         * @return the application result.
         */
        public abstract double perform(final double[] arr);
    }


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
        public final String name;


        /**
         * Constructor.
         * 
         * @param name
         */
        private ThresholdPredicate(final String name) {
            this.name = name;
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

        return operation.equals(e.get("operation"));
    }


    /**
     * @param method
     * @return a {@link ThresholdFold}.
     * @throws ThresholdRuleValidationError
     */
    static ThresholdFold foldFrom(final String method) throws ThresholdRuleValidationError {
        for (final ThresholdFold p : ThresholdFold.values())
            if (p.name.equals(method))
                return p;

        throw new ThresholdRuleValidationError("unsupported aggregation method: " + method);
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
     * @param predicate
     * @return a {@link ThresholdPredicate}.
     * @throws ThresholdRuleValidationError
     */
    static ThresholdPredicate predicateFrom(final String predicate) throws ThresholdRuleValidationError {
        for (final ThresholdPredicate p : ThresholdPredicate.values())
            if (p.name.equals(predicate))
                return p;

        throw new ThresholdRuleValidationError("unsupported predicate: " + predicate);
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


    /**
     * @param pred
     * @param eventValue
     * @param thresholdValue
     * @return <code>true</code> when <code>eventValue</code> has exceeded <code>thresholdValue</code>.
     */
    static boolean thresholdExceededBy(final ThresholdPredicate pred, final double eventValue, final double thresholdValue) {
        return pred.perform(eventValue, thresholdValue);
    }
}
