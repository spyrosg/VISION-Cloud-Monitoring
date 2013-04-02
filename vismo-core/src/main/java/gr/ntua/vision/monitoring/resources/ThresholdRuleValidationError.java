package gr.ntua.vision.monitoring.resources;

/**
 * This is used to report validation error on submitted {@link ThresholdRuleBean}s.
 */
@SuppressWarnings("serial")
public class ThresholdRuleValidationError extends RuntimeException {
    /**
     * Constructor.
     * 
     * @param message
     */
    public ThresholdRuleValidationError(final String message) {
        super(message);
    }
}
