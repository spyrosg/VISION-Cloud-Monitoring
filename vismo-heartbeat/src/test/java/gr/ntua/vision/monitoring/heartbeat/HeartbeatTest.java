package gr.ntua.vision.monitoring.heartbeat;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Test;


public class HeartbeatTest {

    private static final String MULTICAST_IP   = "224.0.0.2";
    private static final int    MULTICAST_PORT = 6000;
    private static final int    TTL            = 0;


    /*
     * 
     */

    @Test
    public void testHeartbeatServiceSuccessfull() throws IOException {

        HeartbeatReceiver receiver = new HeartbeatReceiver(InetAddress.getByName(MULTICAST_IP), MULTICAST_PORT);
        HeartbeatSender sender = new HeartbeatSender(InetAddress.getByName(MULTICAST_IP), MULTICAST_PORT, TTL);
        sender.setHeartBeatInterval(1000);
        receiver.init();
        sender.init();
        sleep(2000);
        assertEquals("We expect membership to be valid", true,checkMembership(receiver.getMembers()));
        sender.halt();
        receiver.halt();
    }


    /*
     * 
     */
    @Test
    public void testHeartbeatServiceUnSuccessfull() throws IOException {

        HeartbeatReceiver receiver = new HeartbeatReceiver(InetAddress.getByName(MULTICAST_IP), MULTICAST_PORT);
        HeartbeatSender sender = new HeartbeatSender(InetAddress.getByName(MULTICAST_IP), MULTICAST_PORT, TTL);
        sender.setHeartBeatInterval(10000);
        receiver.init();
        sender.init();
        sleep(4000);
        assertEquals("We expect membership to be invalid",false,  checkMembership(receiver.getMembers()));
        sender.halt();
        receiver.halt();
    }


    /*
     * 
     */
    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    /*
     * 
     */
    private boolean checkMembership(HashMap<String, Boolean> members) {

        boolean result = true;

        if (members.size() == 0)
            return false;
        else {
            Iterator<String> iterator = members.keySet().iterator();
            while (iterator.hasNext()) {

                result = result && members.get(iterator.next().toString()).booleanValue();
            }
            return result;

        }

    }

}
