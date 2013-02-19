package integration.tests;

import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.rules.ClassPathRulesFactory;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.VismoRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;

import org.junit.Ignore;
import org.junit.Test;


/**
 * 
 */
public class ClassPathRulesFactoryTest {
    /***/
    private final VismoRulesEngine      engine  = new VismoRulesEngine(new RulesStore());
    /***/
    private final ClassPathRulesFactory factory = new ClassPathRulesFactory(engine);


    /***/
    @Test
    public void shouldLoadRulesByFullyQualifiedName() {
        final String FULL_RULE_NAME = PassThroughRule.class.getCanonicalName();
        final VismoRule rule = factory.buildByName(FULL_RULE_NAME);

        assertTrue(rule != null);
        assertTrue(rule instanceof PassThroughRule);
    }


    /***/
    @Ignore("wip")
    @Test
    public void shouldLoadRulesByName() {
        // TODO
    }
}
