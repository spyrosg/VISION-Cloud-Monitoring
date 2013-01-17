package gr.ntua.vision.monitoring.rules.propagation;

import java.io.Serializable;


/**
 * @author tmessini
 */
public class Message implements Serializable {
    /***/
    private static final long serialVersionUID = 1L;
    /***/
    private String            command;
    /***/
    private int               commandId;
    /***/
    private int               fromId;
    /***/
    private int               groupSize;
    /***/
    private volatile int      hashCode;
    /***/
    private MessageType       type;
    /***/
    private long              lastUpdate;




    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((command == null) ? 0 : command.hashCode());
        result = prime * result + commandId;
        result = prime * result + fromId;
        result = prime * result + groupSize;
        result = prime * result + hashCode;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + (int) (lastUpdate ^ (lastUpdate >>> 32));
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Message other = (Message) obj;
        if (command == null) {
            if (other.command != null)
                return false;
        } else if (!command.equals(other.command))
            return false;
        if (commandId != other.commandId)
            return false;
        if (fromId != other.fromId)
            return false;
        if (groupSize != other.groupSize)
            return false;
        if (hashCode != other.hashCode)
            return false;
        if (type != other.type)
            return false;
        if (lastUpdate != other.lastUpdate)
            return false;
        return true;
    }


    /**
     * @return the multicast group
     */
    public String getCommand() {
        return command;
    }


    /**
     * @return the commandId.
     */
    public int getCommandId() {
        return commandId;
    }


    /**
     * @return the process id
     */
    public int getFromId() {
        return fromId;
    }


    /**
     * @return groupsize.
     */
    public int getGroupSize() {
        return groupSize;
    }


    /**
     * @return the type of the message.
     */
    public MessageType getType() {
        return type;
    }





    /**
     * @param command
     */
    public void setCommand(final String command) {
        this.command = command;
    }


    /**
     * @param commandId
     */
    public void setCommandId(final int commandId) {
        this.commandId = commandId;
    }


    /**
     * @param fromId
     */
    public void setFromId(final int fromId) {
        this.fromId = fromId;
    }


    /**
     * @param groupSize
     */
    public void setGroupSize(final int groupSize) {
        this.groupSize = groupSize;
    }


    /**
     * @param addRule
     *            sets the type of the message
     */
    public void setType(final MessageType addRule) {
        this.type = addRule;
    }


    @Override
    public String toString() {
        final String str = "fromId=" + fromId + "command=" + command + "type=" + type + "commandId=" + commandId + "groupSize="
                + groupSize;

        return str;
    }

    /***
     * 
     * @return long
     */
    public long getUpdateDiff() {
        return lastUpdate;
    }

    /***
     * 
     * @param updateDiff
     */
    public void setUpdateDiff(long updateDiff) {
        this.lastUpdate = updateDiff;
    }

}
