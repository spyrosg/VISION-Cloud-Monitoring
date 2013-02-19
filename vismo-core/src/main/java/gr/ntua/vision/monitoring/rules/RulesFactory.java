package gr.ntua.vision.monitoring.rules;

/**
 * This is used to abstract away the details of loading rules into the system.
 */
public interface RulesFactory {
    /**
     * Attempts to construct a rule by name.
     * 
     * @param ruleName
     *            the name of the rule to load.
     * @return on success, an instance of {@link VismoRule}.
     * @throws RuntimeException
     *             on some error
     */
    VismoRule constructByName(String ruleName);


    /**
     * Attempts to construct a rule by name and given arguments.
     * 
     * @param ruleName
     *            the name of the rule to load.
     * @param args
     *            the list of arguments for the rule.
     * @return on success, an instance of {@link VismoRule}.
     * @throws RuntimeException
     *             on some error
     */
    VismoRule constructByNameWithArguments(String ruleName, Object... args);
}
