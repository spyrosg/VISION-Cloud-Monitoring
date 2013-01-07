package gr.ntua.vision.monitoring.rules.propagation;

import gr.ntua.vision.monitoring.rules.AbstractRuleFactory;
import gr.ntua.vision.monitoring.rules.PeriodicRule;
import gr.ntua.vision.monitoring.rules.Rule;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class MessageDeliverer extends Thread implements Observer {
    /***/
    private final static Logger       log     = LoggerFactory.getLogger(RulesPropagationManager.class);
    /***/
    private final AbstractRuleFactory factory = new AbstractRuleFactory();
    /***/
    private RulesPropagationManager   manager;


    @Override
    public void run() {
        Message deliveredMessage;
        while (true)
            if (!manager.getDelQueue().isQEmpty()) {
                deliveredMessage = manager.getDelQueue().getMessage();
                final String type = deliveredMessage.getType();
                final String rule = deliveredMessage.getCommand();
                final int ruleId = deliveredMessage.getCommandId();

                String ruleName = "";
                String rulePeriod = "";
                String ruleDesc = "";

                if (rule != null && rule.contains(":")) {
                    final String[] ruleParts = rule.split(":");
                    if (ruleParts.length == 3) {
                        ruleName = ruleParts[0];
                        rulePeriod = ruleParts[1];
                        ruleDesc = ruleParts[2];
                    }
                }

                Object ruleObject = null;
                if (factory.createRuleFactory(ruleName) != null)
                    ruleObject = factory.createRuleFactory(ruleName).createRule(manager.getEngine(), rulePeriod, ruleName,
                                                                                ruleDesc);
                if (ruleObject != null)
                    processObject(ruleObject, type, rule, ruleId);

            } else
                synchronized (this) {
                    try {
                        wait();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }

                }
    }


    /**
     * @param manager
     */
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
    }


    @Override
    public void update(final Observable o, final Object m) {
        synchronized (this) {
            notify();
        }
    }


    /**
     * checks whether the engine rules number is the same as the web interface number.
     */
    private void checkEngineHealth() {
        if (manager.getEngine().getRulesTotalNumber() != manager.getRuleStore().getRulesCatalogSize())
            MessageDeliverer.log.debug("ERROR: web rule #: " + manager.getRuleStore().getRulesCatalogSize() + " engine rule#: "
                    + manager.getEngine().getRulesTotalNumber());
        // TODO re-initialize the rules of the engine
    }


    /**
     * process the rule Object
     * 
     * @param ruleObject
     * @param type
     * @param ruleString
     * @param ruleId
     */
    private void processObject(final Object ruleObject, final String type, final String ruleString, final int ruleId) {
        final boolean rule = ruleObject instanceof Rule;
        final boolean periodicRule = ruleObject instanceof PeriodicRule;

        if (rule && type.equals("add")) {
            ((Rule) ruleObject).submitTo(manager.getEngine());
            manager.getRuleStore().addRule(ruleString, ruleId);
            checkEngineHealth();
        }
        if (rule && type.equals("del")) {
            manager.getEngine().removeRule((Rule) ruleObject);
            manager.getRuleStore().deleteRule(ruleString);
            checkEngineHealth();
        }
        if (periodicRule && type.equals("add")) {
            ((PeriodicRule) ruleObject).submitTo(manager.getEngine());
            manager.getRuleStore().addRule(ruleString, ruleId);
            checkEngineHealth();
        }
        if (periodicRule && type.equals("del")) {
            manager.getEngine().removeRule((PeriodicRule) ruleObject);
            manager.getRuleStore().deleteRule(ruleString);
            checkEngineHealth();
        }
    }
}
