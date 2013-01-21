package gr.ntua.vision.monitoring.threading;

import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.mon.VismoGroupClient;

import java.io.IOException;
import java.net.UnknownHostException;

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
        super(period);
        this.client = new VismoGroupClient(conf);
        this.vminfo = vminfo;
    }


    /**
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        log.debug("contacting group");

        try {
            client.notifyGroup(this + ":" + vminfo.getAddress().getHostAddress());
        } catch (final IOException e) {
            log.error("unable to contact group", e);
        }
    }
}
