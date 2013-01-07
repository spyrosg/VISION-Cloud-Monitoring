package gr.ntua.vision.monitoring.rules.propagation;

import java.io.Serializable;


/**
 * @author tmessini
 */
public class Message implements Serializable {
    /***/
    private static final long serialVersionUID = 1L;
    /***/
    private int               commandId;
    /***/
    private int               fromId;
    /***/
    private int               groupSize;
    /***/
    private volatile int      hashCode;
    /***/
    private String            toGroup;
    /***/
    private String            type;


    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Message other = (Message) obj;
        if (commandId != other.commandId)
            return false;
        if (fromId != other.fromId)
            return false;
        if (groupSize != other.groupSize)
            return false;
        if (hashCode != other.hashCode)
            return false;
        if (toGroup == null) {
            if (other.toGroup != null)
                return false;
        } else if (!toGroup.equals(other.toGroup))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }


    /**
     * @return the multicast group
     */
    public String getCommand() {
        return toGroup;
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
    public String getType() {
        return type;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + commandId;
        result = prime * result + fromId;
        result = prime * result + groupSize;
        result = prime * result + hashCode;
        result = prime * result + ((toGroup == null) ? 0 : toGroup.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }


    /**
     * @param toGroup
     */
    public void setCommand(final String toGroup) {
        this.toGroup = toGroup;
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
     * @param type
     *            sets the type of the message
     */
    public void setType(final String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        final String str = "fromId=" + fromId + "toGroup=" + toGroup + "type=" + type + "commandId=" + commandId + "groupSize="
                + groupSize;

        return str;
    }

}
