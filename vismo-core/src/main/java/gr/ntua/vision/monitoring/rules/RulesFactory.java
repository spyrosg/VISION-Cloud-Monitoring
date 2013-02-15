package gr.ntua.vision.monitoring.rules;

/**
 * This is used to abstract away the details of loading rules into the system.
 */
public interface RulesFactory {
    /**
     * Attempts to build a rule by name.
     * 
     * @param ruleName
     *            the name of the rule to load.
     * @return on success, an instance of {@link VismoRule}.
     * @throws RuntimeException
     *             on some error
     */
    VismoRule buildByName(String ruleName);
}
