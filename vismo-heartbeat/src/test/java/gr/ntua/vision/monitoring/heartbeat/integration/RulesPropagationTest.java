package gr.ntua.vision.monitoring.heartbeat.integration;

import gr.ntua.vision.monitoring.heartbeat.HeartbeatReceiver;
import gr.ntua.vision.monitoring.heartbeat.HeartbeatSender;
import gr.ntua.vision.monitoring.web.RulesWebServer;



import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RulesPropagationTest {
    
    private static final Logger log            = LoggerFactory.getLogger(RulesPropagationTest.class);
    private final static String        MULTICAST_IP   = "224.0.0.1";
    private final static int           MULTICAST_PORT = 6307;
    private final static int           TTL            = 1;
    
          
       
    /**
     * testing the functionality. 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        
        //starting the rest service
        final RulesWebServer webService = new RulesWebServer();
        webService.start();
                        
        //starting the heartbeat receiver
        final HeartbeatReceiver receiver = new HeartbeatReceiver(InetAddress.getByName(MULTICAST_IP), MULTICAST_PORT);
        receiver.clearMembership();
        receiver.init();        
        
        //starting a heartbeat sender
        final HeartbeatSender sender = new HeartbeatSender(InetAddress.getByName(MULTICAST_IP), MULTICAST_PORT, TTL);
        sender.setHeartBeatInterval(1000);
        sender.init();
        
        
        
   

    }

    

}
