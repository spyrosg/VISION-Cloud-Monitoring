package gr.ntua.vision.monitoring.rules.propagation;

import gr.ntua.vision.monitoring.heartbeat.HeartbeatReceiver;
import gr.ntua.vision.monitoring.heartbeat.HeartbeatSender;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.rules.propagation.com.MessageMulticastReceiver;
import gr.ntua.vision.monitoring.rules.propagation.com.MessageMulticastSender;
import gr.ntua.vision.monitoring.rules.propagation.message.MessageMap;
import gr.ntua.vision.monitoring.rules.propagation.message.MessageQueue;
import gr.ntua.vision.monitoring.rules.propagation.resource.RulesManagementResource;
import gr.ntua.vision.monitoring.rules.propagation.services.ClusterRulesResolver;
import gr.ntua.vision.monitoring.rules.propagation.services.Elector;
import gr.ntua.vision.monitoring.rules.propagation.services.MessageDeliverer;
import gr.ntua.vision.monitoring.rules.propagation.services.MessageDispatcher;
import gr.ntua.vision.monitoring.rules.propagation.services.WatchDog;
import gr.ntua.vision.monitoring.rules.propagation.store.ClusterRuleStore;
import gr.ntua.vision.monitoring.rules.propagation.store.NodeRuleStore;
import gr.ntua.vision.monitoring.web.WebServer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class RulesPropagationManager extends Thread {
    /***/
    @SuppressWarnings("unused")
    private final static Logger            log                      = LoggerFactory.getLogger(RulesPropagationManager.class);
    /***/
    private final MessageDeliverer         deliverer                = new MessageDeliverer();
    /***/
    private final MessageQueue             delQueue;
    /***/
    private final MessageDispatcher        dispatcher               = new MessageDispatcher();
    /***/
    private final String                   HEARTBEAT_MULTICAST_IP   = "224.0.0.1";
    /***/
    private final int                      HEARTBEAT_MULTICAST_PORT = 6307;
    /***/
    private final HeartbeatReceiver        heartbeatReceiver;
    /***/
    private final HeartbeatSender          heartbeatSender;
    /***/
    private final MessageQueue             inputQueue;
    /***/
    private final MessageMap               messageCounter           = new MessageMap();
    /***/
    private final MessageMulticastReceiver          messageReceiver          = new MessageMulticastReceiver();
    /***/
    private final MessageMulticastSender            messageSender            = new MessageMulticastSender();
    /***/
    private final MessageMap               messageTimestamp         = new MessageMap();
    /***/
    private final WatchDog messageWatchdog;
    /***/
    private final MessageQueue             outQueue;
    /***/
    private final MessageQueue             outUnicastQueue;
    /***/
    private Integer                        pid;
    /***/
    private final NodeRuleStore                ruleStore;
    /***/
    private final ClusterRuleStore              clusterRuleStore;
    /***/
    private int                            size;
    /***/
    private final VismoRulesEngine         vismoRulesEngine;
    /***/
    private final WebServer           webServer;
    /***/
    private final Elector elector;
    /***/
    private final ClusterRulesResolver clusterRulesResolver = new ClusterRulesResolver(20000, this);

    /**
     * @param engine
     * @param serverPort
     * @throws IOException
     */
    public RulesPropagationManager(final VismoRulesEngine engine, final int serverPort) throws IOException {
        setPid();
        vismoRulesEngine = engine;
        ruleStore = new NodeRuleStore();
        clusterRuleStore = new ClusterRuleStore();
        webServer = new WebServer(serverPort);
        heartbeatReceiver = new HeartbeatReceiver(InetAddress.getByName(HEARTBEAT_MULTICAST_IP), HEARTBEAT_MULTICAST_PORT);
        heartbeatSender = new HeartbeatSender(InetAddress.getByName(HEARTBEAT_MULTICAST_IP), HEARTBEAT_MULTICAST_PORT, 1,
                getPid());
        messageWatchdog = new WatchDog(10000);

        messageReceiver.setManager(this);
        messageSender.setManager(this);
        dispatcher.setManager(this);
        deliverer.setManager(this);
        messageWatchdog.setManager(this);



        inputQueue = new MessageQueue();
        outQueue = new MessageQueue();
        delQueue = new MessageQueue();
        outUnicastQueue = new MessageQueue();

        inputQueue.addObserver(dispatcher);
        outQueue.addObserver(messageSender);
        delQueue.addObserver(deliverer);
        
        elector = new  Elector(this);
    }


    /**
     * @return the deleted queue
     */
    public MessageQueue getDelQueue() {
        return delQueue;
    }


    /**
     * @return vision monitoring rules engine
     */
    public VismoRulesEngine getEngine() {
        return vismoRulesEngine;

    }


    /**
     * @return the heart beat receiver
     */
    public HeartbeatReceiver getHeartbeatReceiver() {
        return heartbeatReceiver;
    }

    /**
     * @return resolver
     */
    public ClusterRulesResolver getClusterRulesResolver(){
        return clusterRulesResolver;
    }

    /**
     * @return the input queue.
     */
    public MessageQueue getInQueue() {
        return inputQueue;
    }





    /**
     * @return the output queue.
     */
    public MessageMap getMessageCounter() {
        return messageCounter;
    }


    /**
     * @return the output queue.
     */
    public MessageMap getMessageTimestamp() {
        return messageTimestamp;
    }


    /**
     * @return the output queue.
     */
    public MessageQueue getOutQueue() {
        return outQueue;
    }


    /**
     * returns the unique id of the node
     * 
     * @return id
     */
    public Integer getPid() {
        return pid;
    }


    /**
     * @return integer
     */
    @SuppressWarnings("static-method")
    public Integer getRandomID() {
        final Random randomGenerator = new Random();
        return Integer.valueOf(randomGenerator.nextInt(100000) + 100000);
    }

    /**
     * returns whether pid is elected
     * @return whether the node is elected
     */
    public boolean isElected()
    {
        return elector.isElected();
    }

    /**
     * common place to store rules
     * 
     * @return ruleStore
     */
    public NodeRuleStore getRuleStore() {
        return ruleStore;
    }

    /**
     * common place to store cluster rules
     * 
     * @return ruleStore
     */
    public ClusterRuleStore getClusterRuleStore() {
        return clusterRuleStore;
    }


    /**
     * @return size.
     */
    public int getSize() {
        return size;
    }


    /**
     * 
     */
    public void halt() {
        try {
            webServer.stop();
            messageSender.halt();
            messageReceiver.halt();
            dispatcher.halt();
            deliverer.halt();
            heartbeatReceiver.halt();
            heartbeatSender.halt();
            messageWatchdog.cancel();
            elector.cancel();
            clusterRulesResolver.cancel();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (true)
            try {
                Thread.sleep(100000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        // TODO
    }


    /***/
    public void setPid() {
        pid = getRandomID();
    }


    /**
     * @see java.lang.Thread#start()
     */
    @Override
    public synchronized void start() {
        try {
            webServer.withResource(new RulesManagementResource(this));
            webServer.start();
            messageSender.init();
            messageReceiver.init();
            dispatcher.start();
            deliverer.start();
            heartbeatReceiver.init();
            heartbeatSender.init();
            messageWatchdog.scheduleWith(new Timer());
            elector.scheduleWith(new Timer());
            clusterRulesResolver.scheduleWith(new Timer());
        } catch (final Throwable x) {
            throw new RuntimeException(x);
        }

        super.start();
    }


    /**
     * @return messageQueue
     */
    public MessageQueue getOutUnicastQueue() {
        return outUnicastQueue;
    }
}
