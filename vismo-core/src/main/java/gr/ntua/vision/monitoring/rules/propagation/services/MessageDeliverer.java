package gr.ntua.vision.monitoring.rules.propagation.services;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AbstractRuleFactory;
import gr.ntua.vision.monitoring.rules.RuleProc;
import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.rules.propagation.message.Message;
import gr.ntua.vision.monitoring.rules.propagation.message.MessageFactory;
import gr.ntua.vision.monitoring.rules.propagation.message.MessageType;

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
                // log.info(deliveredMessage.toString());
                processDeliveredMessage(deliveredMessage);
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
    private RuleProc<Event> getRule(final String rule) {
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
        final Integer ruleId = new Integer(deliveredMessage.getCommandId());

        if (type.equals(MessageType.ADD_RULE)) {
            final RuleProc<Event> ruleObject = getRule(rule);
            if (ruleObject != null) {
                ruleObject.submit();
                manager.getRuleStore().addRule(rule, ruleId);
                checkEngineHealth();
            }
        }
        if (type.equals(MessageType.DELETE_RULE)) {
            final RuleProc<Event> ruleObject = getRule(rule);
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
        // log.info("rules message received from:"+deliveredMessage.getFromId()+":"+ ruleId);

        if (type.equals(MessageType.SET_RULES)) {
            // /System.out.println(deliveredMessage.toString());
            /*
            if(deliveredMessage.getRuleSet()!=null)
            if (deliveredMessage.getRuleSet().equals(manager.getRuleStore().getRulesMap()))
            {
                
            }
                //log.info(manager.getPid() + ": rules are synchronized with cluster");
            else {
                // we start the synchronization process
                // remove what is inside
                Iterator<Integer> iterDel = manager.getRuleStore().getRulesMap().keySet().iterator();
                while (iterDel.hasNext()) {
                    Integer key = iterDel.next();
                    RuleProc<Event> ruleObject = getRule(manager.getRuleStore().getRule(key));
                    if (ruleObject != null) {
                        manager.getEngine().removeRule(ruleObject);
                        manager.getRuleStore().deleteRule(key);
                        checkEngineHealth();
                    }
                }
                // make sure is empty
                if (manager.getRuleStore().getRulesMap().size() == 0) {
                    // put the rule set from elected host
                    manager.getRuleStore().setRulesMap(deliveredMessage.getRuleSet());

                    Iterator<Integer> iterPut = manager.getRuleStore().getRulesMap().keySet().iterator();
                    while (iterPut.hasNext()) {
                        Integer key = iterPut.next();

                        RuleProc<Event> ruleObject = getRule(manager.getRuleStore().getRule(key));
                        if (ruleObject != null) {
                            ruleObject.submit();
                            manager.getRuleStore().addRule(manager.getRuleStore().getRule(key), key);
                            checkEngineHealth();
                        }

                    }
                    
                    //we try to synchronize the update
                    manager.getRuleStore().setLastChanged(System.currentTimeMillis() - deliveredMessage.getUpdateDiff());
                    
                    //log.info(manager.getPid()+":Rules just Synchronized!");

                }


            }
            */
        }

    }

}
