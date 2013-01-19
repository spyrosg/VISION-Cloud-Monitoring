package gr.ntua.vision.monitoring.mon;

import gr.ntua.vision.monitoring.VismoConfiguration;

import java.io.IOException;
import java.net.UnknownHostException;


/**
 *
 */
public class VismoGroupClient {
    /***/
    private final GroupClient client;


    /**
     * @param conf
     * @throws UnknownHostException
     */
    public VismoGroupClient(final VismoConfiguration conf) throws UnknownHostException {
        this.client = new GroupClient(conf.getMonGroupAddr(), conf.getMonGroupPort());
    }


    /**
     * @param note
     * @throws IOException
     * @see gr.ntua.vision.monitoring.mon.GroupClient#notifyGroup(java.lang.String)
     */
    public void notifyGroup(final String note) throws IOException {
        client.notifyGroup(note);
    }
}
