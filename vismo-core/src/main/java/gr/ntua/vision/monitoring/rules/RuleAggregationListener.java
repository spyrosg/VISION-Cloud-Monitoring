package gr.ntua.vision.monitoring.rules;

/**
 * 
 */
public interface RuleAggregationListener {
    /**
     * @param rule
     * @param result
     */
    void endAggregation(final AggregationRule rule, final AggregationResult result);


    /**
     * @param rule
     */
    void startAggregation(final AggregationRule rule);
}
