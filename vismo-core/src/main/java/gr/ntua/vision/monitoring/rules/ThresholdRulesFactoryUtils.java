package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.resources.ThresholdRuleValidationError;


/**
 * 
 */
class ThresholdRulesFactoryUtils {
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


    /**
     * Constructor.
     */
    private ThresholdRulesFactoryUtils() {
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
