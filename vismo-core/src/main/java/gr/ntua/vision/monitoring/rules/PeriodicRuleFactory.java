package gr.ntua.vision.monitoring.rules;

/***
 * @author tmessini
 */
public class PeriodicRuleFactory implements RuleFactory {

    @Override
    public Object createRule(final VismoRulesEngine vismoRulesEngine, final String period, final String name, final String desc) {
        if (name.equals("AccountingRule"))
            return new AccountingRule(vismoRulesEngine, Long.valueOf(period));
        if (name.equals("CTORule"))
            return new CTORule(vismoRulesEngine, desc, Long.valueOf(period));
        return null;

    }

}
