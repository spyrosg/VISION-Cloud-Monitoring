package gr.ntua.vision.monitoring.rules;

/***
 * @author tmessini
 */
public class NonPeriodicRuleFactory implements RuleFactory {

    @Override
    public Object createRule(final VismoRulesEngine vismoRulesEngine, final String period, final String name, final String desc) {
        if (name.equals("TestingRule"))
            return new TestingRule(vismoRulesEngine, name, desc);
        return null;
    }

}
