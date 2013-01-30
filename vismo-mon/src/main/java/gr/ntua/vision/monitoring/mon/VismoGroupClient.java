package gr.ntua.vision.monitoring.mon;

import gr.ntua.vision.monitoring.VismoConfiguration;

import java.io.IOException;
import java.net.UnknownHostException;


/**
 * A client for talking to a vismo group.
 */
public class VismoGroupClient {
    /** the actual client */
    private final GroupClient client;


    /**
     * Constructor.
     * 
     * @param conf
     *            the configuration object.
     * @throws UnknownHostException
     */
    public VismoGroupClient(final VismoConfiguration conf) throws UnknownHostException {
        this.client = new GroupClient(conf.getMonGroupAddr(), conf.getMonGroupPort());
    }


    /**
     * Notify the vismo group.
     * 
     * @param note
     *            the note to send.
     * @throws IOException
     * @see gr.ntua.vision.monitoring.mon.GroupClient#notifyGroup(java.lang.String)
     */
    public void notifyGroup(final String note) throws IOException {
        client.notifyGroup(note);
    }
}
