package gr.ntua.vision.monitoring.rules;

/***
 * @author tmessini
 */
public class NonPeriodicRuleFactory implements RuleFactory {

    /**
     * @see gr.ntua.vision.monitoring.rules.RuleFactory#createRule(gr.ntua.vision.monitoring.rules.VismoRulesEngine,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Rule createRule(final VismoRulesEngine vismoRulesEngine, final String period, final String name, final String desc) {
        if (name.equals("TestingRule"))
            return new TestingRule(vismoRulesEngine, name, desc);
        return null;
    }

}
