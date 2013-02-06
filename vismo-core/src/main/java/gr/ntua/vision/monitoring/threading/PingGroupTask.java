package gr.ntua.vision.monitoring.threading;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.mon.VismoGroupClient;

import java.io.IOException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to periodically contact the vismo group.
 */
public class PingGroupTask extends PeriodicTask {
    /** the log target. */
    private static final Logger    log = LoggerFactory.getLogger(PingGroupTask.class);
    /** the vismo group client. */
    private final VismoGroupClient client;
    /***/
    private final String           note;


    /**
     * Constructor.
     * 
     * @param conf
     *            the configuration object.
     * @param note
     * @throws UnknownHostException
     */
    public PingGroupTask(final VismoConfiguration conf, final String note) throws UnknownHostException {
        super(conf.getMonPingPeriod());
        this.client = new VismoGroupClient(conf);
        this.note = note;
    }


    /**
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        try {
            client.notifyGroup(note);
        } catch (final IOException e) {
            log.error("unable to contact group", e);
            // move on
        }
    }
}
