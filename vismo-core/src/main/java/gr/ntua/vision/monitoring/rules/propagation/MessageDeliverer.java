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
    private final static Logger       log     = LoggerFactory.getLogger(MessageDeliverer.class);
    /***/
    private final AbstractRuleFactory factory = new AbstractRuleFactory();
    /***/
    private RulesPropagationManager   manager;
    /***/


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
                //log.info(deliveredMessage.toString());
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
     * process the delivered Message
     * 
     * @param deliveredMessage
     */
    private void processDeliveredMessage(final Message deliveredMessage) {
        final MessageType type = deliveredMessage.getType();
        final String rule = deliveredMessage.getCommand();
        final Integer ruleId = new Integer(deliveredMessage.getCommandId());
    
        if (type.equals(MessageType.ADD_RULE)) {
            RuleProc<Event> ruleObject = getRule(rule);
            if (ruleObject != null){
            ruleObject.submit();
            manager.getRuleStore().addRule(rule, ruleId);
            checkEngineHealth();
            }
        }
        if (type.equals(MessageType.DELETE_RULE)) {
            RuleProc<Event> ruleObject = getRule(rule);
            if (ruleObject != null){
            manager.getEngine().removeRule(ruleObject);
            manager.getRuleStore().deleteRule(ruleId);
            checkEngineHealth();
            }
        }
        
        if (type.equals(MessageType.GET_RULES)) {
            final Message m = new Message();
            m.setGroupSize(1);
            m.setCommandId(ruleId);
            m.setType(MessageType.RULES);
            m.setCommand(manager.getRuleStore().getRules());
            m.setUpdateDiff(manager.getRuleStore().getLastChangedDiff());
            manager.getOutQueue().addMessage(m);                          
        }
        
        if (type.equals(MessageType.RULES)&& manager.isElected()) {
            manager.getRulesResolutionQueue().addMessage(deliveredMessage);                                      
        }
        
        
    }
    
    /**
     * creates the rule object
     * 
     * @param rule
     * @return rule object
     */
    private RuleProc<Event> getRule(String rule)
    {
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
        return ruleObject;
    }
    
}
