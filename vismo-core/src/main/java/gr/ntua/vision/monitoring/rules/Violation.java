package gr.ntua.vision.monitoring.rules;

import java.util.List;


/**
 * TODO: should provide specific filterUnit
 */
public class Violation {
    /***/
    public final double       eventValue;
    /***/
    public final List<String> filterUnits;
    /***/
    public final String       metric;
    /***/
    public final double       threshold;


    /**
     * Constructor.
     * 
     * @param metric
     * @param threshold
     * @param eventValue
     * @param filterUnits
     */
    public Violation(final String metric, final double threshold, final double eventValue, final List<String> filterUnits) {
        this.metric = metric;
        this.threshold = threshold;
        this.eventValue = eventValue;
        this.filterUnits = filterUnits;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<Violation: " + metric + ", on " + filterUnits + ", threshold: " + threshold + ", observed value: " + eventValue
                + ">";
    }
}
