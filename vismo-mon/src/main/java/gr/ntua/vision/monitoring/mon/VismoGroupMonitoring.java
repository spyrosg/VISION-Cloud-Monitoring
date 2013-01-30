package gr.ntua.vision.monitoring.mon;

import gr.ntua.vision.monitoring.VismoConfiguration;

import java.net.UnknownHostException;
import java.util.Timer;


/**
 *
 */
public class VismoGroupMonitoring {
    /***/
    private static final long        TWO_SECONDS = 2 * 1000;
    /***/
    private final VismoConfiguration conf;
    /***/
    private final Timer              timer       = new Timer(true);


    /**
     * @param conf
     */
    public VismoGroupMonitoring(final VismoConfiguration conf) {
        this.conf = conf;
    }


    /**
     * Start monitoring.
     * 
     * @param printPeriod
     *            the print period, in millies.
     * @throws UnknownHostException
     */
    public void start(final long printPeriod) throws UnknownHostException {
        final VismoGroupServer server = new VismoGroupServer(conf);
        final Thread t = new Thread(server, "group-server");
        final GroupMembership mship = new GroupMembership(2 * conf.getMonPingPeriod() + 1);

        server.register(new AddGroupMember(mship));
        timer.schedule(new PrintGroupTask(mship), TWO_SECONDS, printPeriod);
        t.start();
    }
}
