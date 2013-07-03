package integration.tests;

import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.rules.AccountingRule;
import gr.ntua.vision.monitoring.rules.ClassPathRulesFactory;
import gr.ntua.vision.monitoring.rules.DefaultRuleBean;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.Rule;
import gr.ntua.vision.monitoring.rules.VismoRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;

import org.junit.Test;


/**
 * 
 */
public class ClassPathRulesFactoryTest {
    /**
     * 
     */
    public static class DummyRule extends Rule {
        /**
         * Constructor.
         * 
         * @param engine
         */
        public DummyRule(final VismoRulesEngine engine) {
            super(engine);
        }


        /**
         * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
         */
        @Override
        public void performWith(final MonitoringEvent c) {
            // NOP
        }
    }

    /***/
    private final VismoRulesEngine engine = new VismoRulesEngine();


    /***/
    @Test
    public void shouldLoadAccountingRule() {
        final ClassPathRulesFactory factory = new ClassPathRulesFactory(engine, PassThroughRule.class.getPackage());
        final DefaultRuleBean bean = new DefaultRuleBean("AccountingRule", 1000l);

        final VismoRule rule = factory.buildFrom(bean);

        assertTrue(rule != null);
        assertTrue(rule instanceof AccountingRule);
    }


    /***/
    @Test
    public void shouldLoadInnerClassRule() {
        final ClassPathRulesFactory factory = new ClassPathRulesFactory(engine, DummyRule.class.getPackage());
        final String RULE_NAME = "DummyRule";
        final DefaultRuleBean bean = new DefaultRuleBean();

        bean.setName(RULE_NAME);
        final VismoRule rule = factory.buildFrom(bean);

        assertTrue(rule != null);
        assertTrue(rule instanceof DummyRule);
    }


    /***/
    @Test
    public void shouldLoadRulesByClassName() {
        final ClassPathRulesFactory factory = new ClassPathRulesFactory(engine, PassThroughRule.class.getPackage());
        final String RULE_NAME = "PassThroughRule";
        final DefaultRuleBean bean = new DefaultRuleBean();

        bean.setName(RULE_NAME);
        final VismoRule rule = factory.buildFrom(bean);

        assertTrue(rule != null);
        assertTrue(rule instanceof PassThroughRule);
    }


    /***/
    @Test
    public void shouldLoadRulesByFullyQualifiedName() {
        final ClassPathRulesFactory factory = new ClassPathRulesFactory(engine, PassThroughRule.class.getPackage());
        final String FULL_RULE_NAME = PassThroughRule.class.getCanonicalName();
        final DefaultRuleBean bean = new DefaultRuleBean();

        bean.setName(FULL_RULE_NAME);
        final VismoRule rule = factory.buildFrom(bean);

        assertTrue(rule != null);
        assertTrue(rule instanceof PassThroughRule);
    }
}
