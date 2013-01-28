package gr.ntua.vision.monitoring.rules.propagation.message;

/**
 * This interface is the factory for messages creation
 * 
 * @author tmessini
 */
public interface AbstractMessageFactory {
    /**
     * @param type
     * @return Message
     */
    public Message createMessage(MessageType type);


    /**
     * @param type
     * @param id
     * @return Message
     */
    public Message createMessage(MessageType type, Integer id);


    /**
     * @param type
     * @param id
     * @param command
     * @return Message
     */
    public Message createMessage(MessageType type, Integer id, String command);
}
