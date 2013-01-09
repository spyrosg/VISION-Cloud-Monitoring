package gr.ntua.vision.monitoring.rules.propagation;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AbstractRuleFactory;
import gr.ntua.vision.monitoring.rules.RuleProc;

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


    /**
     * 
     */
    public void halt() {
        interrupt();
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        Message deliveredMessage;
        while (!isInterrupted())
            if (!manager.getDelQueue().isQEmpty()) {
                deliveredMessage = manager.getDelQueue().getMessage();
                final String type = deliveredMessage.getType();
                final String rule = deliveredMessage.getCommand();
                final Integer ruleId = new Integer(deliveredMessage.getCommandId());

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

                RuleProc<Event> ruleObject = null;
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


    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
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
    private void processObject(final RuleProc<Event> ruleObject, final String type, final String ruleString, final int ruleId) {
        if (type.equals("add")) {
            ruleObject.submit();
            manager.getRuleStore().addRule(ruleString, ruleId);
            checkEngineHealth();
        }
        if (type.equals("del")) {
            manager.getEngine().removeRule(ruleObject);
            manager.getRuleStore().deleteRule(ruleId);
            checkEngineHealth();
        }
    }
}
