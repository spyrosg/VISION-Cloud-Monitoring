package gr.ntua.vision.monitoring.rules;

/**
 * TODO: should provide specific filterUnit
 */
public class Violation {
    /***/
    public final double eventValue;
    /***/
    public final String metric;
    /***/
    public final double threshold;


    /**
     * Constructor.
     * 
     * @param metric
     * @param threshold
     * @param eventValue
     */
    public Violation(final String metric, final double threshold, final double eventValue) {
        this.metric = metric;
        this.threshold = threshold;
        this.eventValue = eventValue;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<Violation: " + metric + ", threshold: " + threshold + ", observed value: " + eventValue + ">";
    }
}
