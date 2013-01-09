package gr.ntua.vision.monitoring.rules;

/***
 * @author tmessini
 */
public class PeriodicRuleFactory implements RuleFactory {
    /**
     * @see gr.ntua.vision.monitoring.rules.RuleFactory#createRule(gr.ntua.vision.monitoring.rules.VismoRulesEngine,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public PeriodicRule createRule(final VismoRulesEngine vismoRulesEngine, final String period, final String name,
            final String desc) {
        if (name.equals("AccountingRule"))
            return new AccountingRule(vismoRulesEngine, Long.valueOf(period));
        if (name.equals("CTORule"))
            return new CTORule(vismoRulesEngine, desc, Long.valueOf(period));
        return null;

    }

}
