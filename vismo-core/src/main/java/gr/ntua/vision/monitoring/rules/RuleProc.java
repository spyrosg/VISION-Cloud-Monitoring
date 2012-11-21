package gr.ntua.vision.monitoring.rules;

/**
 * This is used to encapsulate a rule in a Rules Engine. Traditionally, a rule couples together a condition (or
 * <strong>when</strong> part) and an action (or <strong>when</strong> part). Through the life cycle of a rules engine, a rule is
 * checked against varying conditions that might match a rule, which will consequently trigger the accompanying action.
 * 
 * @param <C>
 *            This stands for the type of the condition that might trigger a rule.
 */
public interface RuleProc<C> {
    /**
     * @param e
     */
    void performWith(final C e);
}
