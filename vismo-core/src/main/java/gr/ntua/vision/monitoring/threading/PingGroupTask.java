package gr.ntua.vision.monitoring.threading;

import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.mon.VismoGroupClient;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class PingGroupTask extends PeriodicTask {
    /***/
    private static final Logger    log = LoggerFactory.getLogger(PingGroupTask.class);
    /***/
    private final VismoGroupClient client;
    /***/
    private final long             period;
    /***/
    private final VMInfo           vminfo;


    /**
     * Constructor.
     * 
     * @param conf
     * @param vminfo
     * @param period
     * @throws UnknownHostException
     */
    public PingGroupTask(final VismoConfiguration conf, final VMInfo vminfo, final long period) throws UnknownHostException {
        this.client = new VismoGroupClient(conf);
        this.vminfo = vminfo;
        this.period = period;
    }


    /**
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        log.debug("contacting group");

        try {
            client.notifyGroup("foo" + ":" + vminfo.getAddress().getHostAddress());
        } catch (final IOException e) {
            log.error("unable to contact group", e);
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.threading.PeriodicTask#scheduleWith(java.util.Timer)
     */
    @Override
    public void scheduleWith(final Timer timer) {
        timer.schedule(this, 0, period);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<PingGroupTask, running every " + period / 1000 + " seconds>";
    }
}
