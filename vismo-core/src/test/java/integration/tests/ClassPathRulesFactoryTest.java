package integration.tests;

import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.rules.ClassPathRulesFactory;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.Rule;
import gr.ntua.vision.monitoring.rules.RulesStore;
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
    public static class FooRule extends Rule {
        /***/
        private final String id;


        /**
         * Constructor.
         * 
         * @param engine
         */
        public FooRule(final VismoRulesEngine engine, final String id) {
            super(engine);
            this.id = id;
        }


        /**
         * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
         */
        @Override
        public void performWith(final MonitoringEvent c) {
            // ignored
        }
    }

    /***/
    private final VismoRulesEngine engine = new VismoRulesEngine(new RulesStore());


    /***/
    @Test
    public void shouldLoadRulesByClassName() {
        final ClassPathRulesFactory factory = new ClassPathRulesFactory(PassThroughRule.class.getPackage(), engine);
        final String RULE_NAME = "PassThroughRule";
        final VismoRule rule = factory.constructByName(RULE_NAME);

        assertTrue(rule != null);
        assertTrue(rule instanceof PassThroughRule);
    }


    /***/
    @Test
    public void shouldLoadRulesByFullyQualifiedName() {
        final ClassPathRulesFactory factory = new ClassPathRulesFactory(PassThroughRule.class.getPackage(), engine);
        final String FULL_RULE_NAME = PassThroughRule.class.getCanonicalName();
        final VismoRule rule = factory.constructByName(FULL_RULE_NAME);

        assertTrue(rule != null);
        assertTrue(rule instanceof PassThroughRule);
    }


    /***/
    @Test
    public void shouldLoadRuleWithArguments() {
        final ClassPathRulesFactory factory = new ClassPathRulesFactory(FooRule.class.getPackage(), engine);
        final VismoRule rule = factory.constructByNameWithArguments("FooRule", "my-rule");

        assertTrue(rule != null);
        assertTrue(rule instanceof FooRule);
    }
}
