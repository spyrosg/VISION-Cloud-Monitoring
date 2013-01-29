package gr.ntua.vision.monitoring.mon;

import gr.ntua.vision.monitoring.VismoConfiguration;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Timer;


/**
 * 
 */
public class Main {
    /***/
    private static final long EVERY_TEN_SECONDS = 10 * 1000;
    /** expiration period in milliseconds */
    private static final long expirationPeriod  = 90 * 1000;
    /***/
    private static final long TWO_SECONDS       = 2 * 1000;


    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {
        final VismoConfiguration conf = new VismoConfiguration(args[0]);

        startService(conf);
    }


    /**
     * @param conf
     * @throws UnknownHostException
     */
    public static void startService(final VismoConfiguration conf) throws UnknownHostException {
        final VismoGroupServer server = new VismoGroupServer(conf);
        final Thread t = new Thread(server, "group-server");
        final GroupMembership mship = new GroupMembership(expirationPeriod);
        final Timer timer = new Timer(true);

        server.register(new AddGroupMember(mship));
        timer.schedule(new PrintGroupTask(mship), TWO_SECONDS, EVERY_TEN_SECONDS);
        t.start();
    }
}
