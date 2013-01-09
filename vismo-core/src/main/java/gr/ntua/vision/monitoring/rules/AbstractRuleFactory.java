package gr.ntua.vision.monitoring.rules;

/***
 * @author tmessini
 */
public class AbstractRuleFactory {
    /***
     * returns the appropriate factory
     * 
     * @param type
     * @return appropriate factory.
     */
    @SuppressWarnings("static-method")
    public RuleFactory createRuleFactory(final String type) {
        if (type.equals("TestingRule") || type.equals("PassThroughRule"))
            return new NonPeriodicRuleFactory();
        if (type.equals("AccountingRule"))
            return new PeriodicRuleFactory();
        return null;
    }
}
