package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRequirementBean;
import gr.ntua.vision.monitoring.resources.ThresholdRuleValidationError;

import java.util.List;


/**
 * 
 */
public class ThresholdRequirement {
    /***/
    enum ThresholdFold {
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


        /**
         * @param method
         * @return a {@link ThresholdFold}.
         * @throws ThresholdRuleValidationError
         */
        static ThresholdFold from(final String method) throws ThresholdRuleValidationError {
            for (final ThresholdFold p : ThresholdFold.values())
                if (p.name.equals(method))
                    return p;

            throw new ThresholdRuleValidationError("unsupported aggregation method: " + method);
        }
    }


    /***/
    enum ThresholdPredicate {
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


        /**
         * @param predicate
         * @return a {@link ThresholdPredicate}.
         * @throws ThresholdRuleValidationError
         */
        static ThresholdPredicate from(final String predicate) throws ThresholdRuleValidationError {
            for (final ThresholdPredicate p : ThresholdPredicate.values())
                if (p.name.equals(predicate))
                    return p;

            throw new ThresholdRuleValidationError("unsupported predicate: " + predicate);
        }
    }

    /***/
    private final ThresholdFold      foldMethod;
    /***/
    private final String             metric;
    /***/
    private final ThresholdPredicate pred;
    /***/
    private final double             thresholdValue;


    /**
     * Constructor.
     * 
     * @param bean
     */
    private ThresholdRequirement(final ThresholdRequirementBean bean) {
        this.thresholdValue = bean.getThreshold();
        this.metric = bean.getMetric();
        this.pred = ThresholdPredicate.from(bean.getPredicate());
        this.foldMethod = bean.getAggregationMethod() != null ? ThresholdFold.from(bean.getAggregationMethod()) : null;
    }


    /**
     * Check that the event concerns the specified metric.
     * 
     * @param e
     * @return <code>true</code> iff the event is about the specified metric, <code>false</code> otherwise.
     */
    public boolean isApplicable(final MonitoringEvent e) {
        return e.get(metric) != null;
    }


    /**
     * @param events
     *            the list of events to check for violation metrics.
     * @return the particular monitored metric violation or <code>null</code>, or <code>null</code>.
     */
    public Violation isViolated(final List<MonitoringEvent> events) {
        final double observedValue = performFold(events);
        final boolean res = pred.perform(observedValue, thresholdValue);

        return res ? new Violation(metric, thresholdValue, observedValue) : null;
    }


    /**
     * @param e
     *            the event to check for violation metrics.
     * @return the particular monitored metric violation or <code>null</code>, or <code>null</code>.
     */
    public Violation isViolated(final MonitoringEvent e) {
        final double observedValue = (Double) e.get(metric);
        final boolean res = pred.perform(observedValue, thresholdValue);

        return res ? new Violation(metric, thresholdValue, observedValue) : null;
    }


    /**
     * @param eventsList
     * @return the fold application value.
     */
    private double performFold(final List<MonitoringEvent> eventsList) {
        final double arr[] = new double[eventsList.size()];

        for (int i = 0; i < arr.length; ++i)
            arr[i] = (Double) eventsList.get(i).get(metric);

        return foldMethod.perform(arr);
    }


    /**
     * @param bean
     * @return the {@link ThresholdRequirement}.
     */
    public static ThresholdRequirement from(final ThresholdRequirementBean bean) {
        return new ThresholdRequirement(bean);
    }
}
