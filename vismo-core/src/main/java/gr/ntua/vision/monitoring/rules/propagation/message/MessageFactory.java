package gr.ntua.vision.monitoring.rules.propagation.message;

import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;


/**
 * Message Factory
 * 
 * @author tmessini
 */
public class MessageFactory implements AbstractMessageFactory {
    /***/
    RulesPropagationManager manager;

    /**
     * Constructor.
     * 
     * @param manager
     */
    public MessageFactory(final RulesPropagationManager manager) {
        this.manager = manager;
    }

    @Override
    public Message createMessage(final MessageType type, final Integer commandId) {
             
        if (type.equals(MessageType.RULES)) {
            final Message rulesMessage = new Message();
            rulesMessage.setFromId(manager.getPid().intValue());
            rulesMessage.setGroupSize(1);
            rulesMessage.setCommandId(commandId);
            rulesMessage.setType(MessageType.RULES);
            rulesMessage.setRuleSet(manager.getRuleStore().getRulesMap());
            rulesMessage.setUpdateDiff(manager.getRuleStore().getLastChangedDiff());
            return rulesMessage;
        }
        
        if (type.equals(MessageType.DELETE_RULE)) {
            final Message deleteRuleMessage = new Message();
            deleteRuleMessage.setFromId(manager.getPid().intValue());
            deleteRuleMessage.setGroupSize(manager.getHeartbeatReceiver().getMembers().size());
            deleteRuleMessage.setCommandId(commandId);
            deleteRuleMessage.setType(MessageType.DELETE_RULE);
            deleteRuleMessage.setCommand(manager.getRuleStore().getRule(commandId));
            return deleteRuleMessage;
            
        }
        
  
        
        
        
        
        return null;
    }

    @Override
    public Message createMessage(MessageType type) {
        if (type.equals(MessageType.SET_RULES)) {
            final Message setRulesMessage = new Message();
            setRulesMessage.setFromId(manager.getPid().intValue());
            setRulesMessage.setGroupSize(manager.getHeartbeatReceiver().getHostsTimestamp().size());
            setRulesMessage.setCommandId(manager.getRandomID());
            setRulesMessage.setType(MessageType.SET_RULES);
            setRulesMessage.setRuleSet(manager.getClusterRulesResolver().getValidClusterRuleSet());
            setRulesMessage.setUpdateDiff(manager.getClusterRulesResolver().getValidTimestamp());
            return setRulesMessage;
        }

        if (type.equals(MessageType.GET_RULES)) {
            Message getRulesMessage = new Message();
            getRulesMessage.setFromId(manager.getPid().intValue());
            getRulesMessage.setGroupSize(1);
            getRulesMessage.setCommandId(manager.getRandomID());
            getRulesMessage.setType(MessageType.GET_RULES);
            getRulesMessage.setCommand("");
            return getRulesMessage;
        }
        
        return null;
    }

    @Override
    public Message createMessage(MessageType type, Integer commandId, String command) {
        if (type.equals(MessageType.ADD_RULE)) {
            
            final Message addRuleMessage = new Message();
            addRuleMessage.setFromId(manager.getPid().intValue());
            addRuleMessage.setGroupSize(manager.getHeartbeatReceiver().getMembers().size());
            addRuleMessage.setCommandId(commandId);
            addRuleMessage.setType(MessageType.ADD_RULE);
            addRuleMessage.setCommand(command);
            
            return addRuleMessage;
            
          
        }
        return null;
    }

}
