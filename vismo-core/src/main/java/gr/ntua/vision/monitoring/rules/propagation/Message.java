package gr.ntua.vision.monitoring.rules.propagation;

import java.io.Serializable;
import java.util.HashMap;


/**
 * @author tmessini
 */
public class Message implements Serializable {
    /***/
    private static final long        serialVersionUID = 1L;
    /***/
    private int                      commandId;
    /***/
    private HashMap<Integer, String> content;
    /***/
    private int                      fromId;
    /***/
    private int                      groupSize;
    /***/
    private volatile int             hashCode;
    /***/
    private String                   toGroup;
    /***/
    private String                   type;


    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }

        if (!(o instanceof Message)) {
            return false;
        }

        final Message msg = (Message) o;

        final boolean res = msg.commandId == commandId && msg.content.equals(content) && msg.fromId == fromId
                && msg.groupSize == groupSize && msg.toGroup.equals(toGroup) && msg.type.equals(type);

        return res;
    }


    /**
     * @return the commandId.
     */
    public int getCommandId() {
        return commandId;
    }


    /**
     * @return the content of the packet.
     */
    public HashMap<Integer, String> getContent() {
        return content;
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
     * @return the multicast group
     */
    public String getToGroup() {
        return toGroup;
    }


    /**
     * @return the type of the message.
     */
    public String getType() {
        return type;
    }


    @Override
    public int hashCode() {
        int result = hashCode;

        if (result == 0) {
            result = 17;
            result = 31 * result + commandId;
            result = 31 * result + content.hashCode();
            result = 31 * result + fromId;
            result = 31 * result + groupSize;
            result = 31 * result + toGroup.hashCode();
            result = 31 * result + type.hashCode();
        }
        return result;

    }


    /**
     * @param commandId
     */
    public void setCommandId(final int commandId) {
        this.commandId = commandId;
    }


    /**
     * @param catalog
     *            the content of the packet.
     */
    public void setContent(final HashMap<Integer, String> catalog) {
        this.content = catalog;
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
     * @param toGroup
     */
    public void setToGroup(final String toGroup) {
        this.toGroup = toGroup;
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
