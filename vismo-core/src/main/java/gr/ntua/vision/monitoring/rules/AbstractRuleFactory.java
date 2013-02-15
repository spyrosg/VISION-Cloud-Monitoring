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
    public RulesFactory createRuleFactory(final String type) {
        // if (type.equals("TestingRule"))
        // return new NonPeriodicRuleFactory();
        if (type.equals("AccountingRule") || type.equals("CTORule"))
            return new PeriodicRuleFactory();
        return null;
    }
}
