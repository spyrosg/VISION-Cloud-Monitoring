package gr.ntua.vision.monitoring.heartbeat.unit;

import gr.ntua.vision.monitoring.heartbeat.HeartbeatReceiver;
import gr.ntua.vision.monitoring.heartbeat.HeartbeatSender;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class HeartbeatTest {
    /***/
    private static final Logger log            = LoggerFactory.getLogger(HeartbeatSender.class);
    /***/
    private final String        MULTICAST_IP   = "224.0.0.1";
    /***/
    private final int           MULTICAST_PORT = 6307;
    /***/
    private final int           TTL            = 1;


    /**
     * @param members
     * @return true or false depending on membership.
     */
    private static boolean checkMembership(final HashMap<String, Boolean> members) {
        boolean result = true;
        if (members.size() == 0)
            return false;
        final Iterator<String> iterator = members.keySet().iterator();
        while (iterator.hasNext())
            result = result && members.get(iterator.next().toString()).booleanValue();
        return result;
    }

    /**
     * @throws IOException
     */
    @Test
    public void testHeartbeatServiceSuccessfull() throws IOException {
        HeartbeatTest.log.info("starting HeartbeatServiceSuccessfull test...");

        final HeartbeatReceiver receiver = new HeartbeatReceiver(InetAddress.getByName(MULTICAST_IP), MULTICAST_PORT);
        receiver.clearMembership();
        // initiate the first sender
        final HeartbeatSender sender1 = new HeartbeatSender(InetAddress.getByName(MULTICAST_IP), MULTICAST_PORT, TTL);
        sender1.setHeartBeatInterval(1000);
        // initiate the second sender
        final HeartbeatSender sender2 = new HeartbeatSender(InetAddress.getByName(MULTICAST_IP), MULTICAST_PORT, TTL);
        sender2.setHeartBeatInterval(1000);

        // initiate the second sender
        final HeartbeatSender sender3 = new HeartbeatSender(InetAddress.getByName(MULTICAST_IP), MULTICAST_PORT, TTL);
        sender3.setHeartBeatInterval(1000);

        receiver.init();
        sender1.init();
        sender2.init();
        sender3.init();

        sleep(2000);

        Assert.assertEquals("We expect membership to be valid", true, HeartbeatTest.checkMembership(receiver.getMembers()));

        sender1.halt();
        sender2.halt();
        sender3.halt();
        receiver.halt();
    }


    /**
     * @param time
     */
    private static void sleep(final long time) {
        try {
            Thread.sleep(time);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

    }

}
