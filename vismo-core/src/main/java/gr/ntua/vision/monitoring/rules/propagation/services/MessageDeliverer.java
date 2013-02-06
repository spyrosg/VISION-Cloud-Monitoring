package gr.ntua.vision.monitoring.rules.propagation.services;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.rules.AbstractRuleFactory;
import gr.ntua.vision.monitoring.rules.RuleProc;
import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.rules.propagation.message.Message;
import gr.ntua.vision.monitoring.rules.propagation.message.MessageFactory;
import gr.ntua.vision.monitoring.rules.propagation.message.MessageType;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class MessageDeliverer extends Thread implements Observer {
    /***/
    private final static Logger       log     = LoggerFactory.getLogger(MessageDeliverer.class);
    /***/
    private final AbstractRuleFactory factory = new AbstractRuleFactory();
    /***/
    private RulesPropagationManager   manager;
    /***/
    private MessageFactory            messageFactory;


    /**
     * stopping the thread
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
                processDeliveredMessage(deliveredMessage);
            } else
                synchronized (this) {
                    try {
                        wait();
                    } catch (final InterruptedException e) {
                        // TODO
                    }
                }
    }


    /**
     * @param manager
     */
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
        messageFactory = new MessageFactory(manager);
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
    }


    /**
     * creates the rule object
     * 
     * @param rule
     * @return rule object
     */
    private RuleProc<MonitoringEvent> getRule(final String rule) {
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
        RuleProc<MonitoringEvent> ruleObject = null;
        if (factory.createRuleFactory(ruleName) != null)
            ruleObject = factory.createRuleFactory(ruleName).createRule(manager.getEngine(), rulePeriod, ruleName, ruleDesc);
        return ruleObject;
    }


    /**
     * process the delivered Message
     * 
     * @param deliveredMessage
     */
    private void processDeliveredMessage(final Message deliveredMessage) {
        final MessageType type = deliveredMessage.getType();
        final String rule = deliveredMessage.getCommand();
        final Integer ruleId = Integer.valueOf(deliveredMessage.getCommandId());

        if (type.equals(MessageType.ADD_RULE)) {
            final RuleProc<MonitoringEvent> ruleObject = getRule(rule);
            if (ruleObject != null) {
                ruleObject.submit();
                manager.getRuleStore().addRule(rule, ruleId);
                checkEngineHealth();
            }
        }
        if (type.equals(MessageType.DELETE_RULE)) {
            final RuleProc<MonitoringEvent> ruleObject = getRule(rule);
            if (ruleObject != null) {
                manager.getEngine().removeRule(ruleObject);
                manager.getRuleStore().deleteRule(ruleId);
                checkEngineHealth();
            }
        }

        if (type.equals(MessageType.GET_RULES))
            manager.getOutQueue().addMessage(messageFactory.createMessage(MessageType.RULES, ruleId));

        if (type.equals(MessageType.RULES))
            if (manager.isElected())
                manager.getClusterRuleStore().addNodeRuleSet(deliveredMessage.getRuleSet(), deliveredMessage.getUpdateDiff());
        if (type.equals(MessageType.SET_RULES))
            if (deliveredMessage.getRuleSet() != null)
                if (!deliveredMessage.getRuleSet().equals(manager.getRuleStore().getRulesMap())) {
                    final Iterator<Integer> iterDel = manager.getRuleStore().getRulesMap().keySet().iterator();
                    while (iterDel.hasNext()) {
                        final Integer key = iterDel.next();
                        final RuleProc<MonitoringEvent> ruleObject = getRule(manager.getRuleStore().getRule(key));
                        if (ruleObject != null) {
                            manager.getEngine().removeRule(ruleObject);
                            manager.getRuleStore().deleteRule(key, false);
                            checkEngineHealth();
                        }
                    }
                    manager.getRuleStore().setRulesMap(deliveredMessage.getRuleSet());
                    final Iterator<Integer> iterPut = deliveredMessage.getRuleSet().keySet().iterator();
                    while (iterPut.hasNext()) {
                        final Integer key = iterPut.next();
                        final RuleProc<MonitoringEvent> ruleObject = getRule(manager.getRuleStore().getRule(key));
                        if (ruleObject != null) {
                            ruleObject.submit();
                            checkEngineHealth();
                        }
                    }
                }
    }
}
