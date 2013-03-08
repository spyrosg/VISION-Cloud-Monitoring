package gr.ntua.vision.monitoring.resources;

/**
 * This is just a data holder, the exchange media format for TODO
 */
public class ThresholdRuleBean {
    /***/
    private String aggregationMethod;
    /***/
    private String aggregationUnit;
    /***/
    private String metric;
    /***/
    private String operation;
    /***/
    private long   period = -1;
    /***/
    private String predicate;
    /***/
    private double threshold;
    /***/
    private String topic;


    /**
     * Default constructor.
     */
    public ThresholdRuleBean() {
    }


    /**
     * @return the aggregationMethod
     */
    public String getAggregationMethod() {
        return aggregationMethod;
    }


    /**
     * @return the aggregationUnit
     */
    public String getAggregationUnit() {
        return aggregationUnit;
    }


    /**
     * @return the metric
     */
    public String getMetric() {
        return metric;
    }


    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }


    /**
     * @return the period
     */
    public long getPeriod() {
        return period;
    }


    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }


    /**
     * @return the threshold
     */
    public double getThreshold() {
        return threshold;
    }


    /**
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }


    /**
     * @param aggregationMethod
     *            the aggregationMethod to set
     */
    public void setAggregationMethod(final String aggregationMethod) {
        this.aggregationMethod = aggregationMethod;
    }


    /**
     * @param aggregationUnit
     *            the aggregationUnit to set
     */
    public void setAggregationUnit(final String aggregationUnit) {
        this.aggregationUnit = aggregationUnit;
    }


    /**
     * @param metric
     *            the metric to set
     */
    public void setMetric(final String metric) {
        this.metric = metric;
    }


    /**
     * @param operation
     *            the operation to set
     */
    public void setOperation(final String operation) {
        this.operation = operation;
    }


    /**
     * @param period
     *            the period to set
     */
    public void setPeriod(final long period) {
        this.period = period;
    }


    /**
     * @param predicate
     *            the predicate to set
     */
    public void setPredicate(final String predicate) {
        this.predicate = predicate;
    }


    /**
     * @param threshold
     *            the threshold to set
     */
    public void setThreshold(final double threshold) {
        this.threshold = threshold;
    }


    /**
     * @param topic
     *            the topic to set
     */
    public void setTopic(final String topic) {
        this.topic = topic;
    }
}
