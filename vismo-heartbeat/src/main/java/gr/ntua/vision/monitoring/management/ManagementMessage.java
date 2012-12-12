package gr.ntua.vision.monitoring.management;

import java.io.Serializable;


public class ManagementMessage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3455369411995264525L;
    private String            content;
    private int               fromId;
    private int               toId;


    public String getContent() {
        return content;
    }


    public int getFromId() {
        return fromId;
    }


    public int getToId() {
        return toId;
    }


    public void setContent(final String con) {
        content = con;
    }


    public void setFromId(final int fromid) {
        fromId = fromid;
    }


    public void setToId(final int toid) {
        toId = toid;
    }

}
