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


public class HeartbeatTest {

    private static final String MULTICAST_IP   = "224.0.0.2";
    private static final int    MULTICAST_PORT = 6000;
    private static final int    TTL            = 3;
    
    private static final Logger   log                        = LoggerFactory.getLogger(HeartbeatSender.class);


    /*
     * 
     */
    @Test
    public void testHeartbeatServiceSuccessfull() throws IOException {
        log.info("starting HeartbeatServiceSuccessfull test...");
        

        final HeartbeatReceiver receiver = new HeartbeatReceiver(InetAddress.getByName(HeartbeatTest.MULTICAST_IP),
                HeartbeatTest.MULTICAST_PORT);
        receiver.clearMembership();
        final HeartbeatSender sender = new HeartbeatSender(InetAddress.getByName(HeartbeatTest.MULTICAST_IP),
                HeartbeatTest.MULTICAST_PORT, HeartbeatTest.TTL);
        sender.setHeartBeatInterval(1000);
        receiver.init();
        sender.init();
        sleep(2000);
        Assert.assertEquals("We expect membership to be valid", true, checkMembership(receiver.getMembers()));
        sender.halt();
        receiver.halt();
    }


    /*
     * 
     */
    @Test
    public void testHeartbeatServiceUnSuccessfull() throws IOException {
        log.info("starting HeartbeatServiceUnSuccessfull test...");

        final HeartbeatReceiver receiver = new HeartbeatReceiver(InetAddress.getByName(HeartbeatTest.MULTICAST_IP),
                HeartbeatTest.MULTICAST_PORT);
        receiver.clearMembership();
        final HeartbeatSender sender = new HeartbeatSender(InetAddress.getByName(HeartbeatTest.MULTICAST_IP),
                HeartbeatTest.MULTICAST_PORT, HeartbeatTest.TTL);
        sender.setHeartBeatInterval(10000);
        receiver.init();
        sender.init();
        sleep(4000);
        Assert.assertEquals("We expect membership to be invalid", false, checkMembership(receiver.getMembers()));
        sender.halt();
        receiver.halt();
    }


    /*
     * 
     */
    private boolean checkMembership(final HashMap<String, Boolean> members) {

        boolean result = true;

        if (members.size() == 0)
            return false;
        else {
            final Iterator<String> iterator = members.keySet().iterator();
            while (iterator.hasNext())
                result = result && members.get(iterator.next().toString()).booleanValue();
            return result;

        }

    }


    /*
     * 
     */
    private void sleep(final long time) {
        try {
            Thread.sleep(time);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

    }

}
