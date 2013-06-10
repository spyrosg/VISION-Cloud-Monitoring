package gr.ntua.vision.monitoring.resources;

/**
 * 
 */
public class ThresholdRequirementBean {
    /***/
    private String metric;
    /***/
    private String predicate;
    /***/
    private double threshold;


    /**
     * Constructor.
     */
    public ThresholdRequirementBean() {
    }


    /**
     * @return the metric
     */
    public String getMetric() {
        return metric;
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
     * @param metric
     *            the metric to set
     */
    public void setMetric(final String metric) {
        this.metric = metric;
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
}
