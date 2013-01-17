package gr.ntua.vision.monitoring.rules.propagation;

/**
 * @author tmessini
 */
public enum MessageType {
    /**
     * The message type that is send so as to add a rule in a cluster.
     */
    ADD_RULE,
    /**
     * The message that is send so as to delete a rule in a cluster.
     */
    DELETE_RULE,
    /**
     * The message that is send from elected host so as to do the rules resolution.
     */
    GET_RULES,
    /**
     * The message contains the node rules.
     */
    RULES,
    /**
     * The message that is send from elected host so as to synch the rules in a cluster.
     */
    SET_RULES
}
