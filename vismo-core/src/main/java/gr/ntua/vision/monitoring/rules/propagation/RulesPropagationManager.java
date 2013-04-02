package gr.ntua.vision.monitoring.rules.propagation;

import gr.ntua.vision.monitoring.heartbeat.HeartbeatReceiver;
import gr.ntua.vision.monitoring.heartbeat.HeartbeatSender;
import gr.ntua.vision.monitoring.rules.ClassPathRulesFactory;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.RulesFactory;
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
import gr.ntua.vision.monitoring.web.WebAppBuilder;
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
public class RulesPropagationManager {
    /***/
    @SuppressWarnings("unused")
    private final static Logger            log                      = LoggerFactory.getLogger(RulesPropagationManager.class);
    /***/
    private static final Random            randomGenerator          = new Random();
    /***/
    private final ClusterRulesResolver     clusterRulesResolver     = new ClusterRulesResolver(20000, this);
    /***/
    private final ClusterRuleStore         clusterRuleStore;
    /***/
    private final MessageDeliverer         deliverer;
    /***/
    private final MessageQueue             delQueue;
    /***/
    private final MessageDispatcher        dispatcher               = new MessageDispatcher();
    /***/
    private final Elector                  elector;
    /***/
    private final RulesFactory             factory;
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
    private final MessageMulticastReceiver messageReceiver          = new MessageMulticastReceiver();
    /***/
    private final MessageMulticastSender   messageSender            = new MessageMulticastSender();
    /***/
    private final MessageMap               messageTimestamp         = new MessageMap();
    /***/
    private final WatchDog                 messageWatchdog;
    /***/
    private final MessageQueue             outQueue;
    /***/
    private final int                      pid;
    /***/
    private final NodeRuleStore            ruleStore;
    /***/
    private int                            size;
    /** the timer object. */
    private final Timer                    timer                    = new Timer(true);
    /***/
    private final VismoRulesEngine         vismoRulesEngine;

    /***/
    private final WebServer                webServer;


    /**
     * @param engine
     * @param serverPort
     * @throws IOException
     */
    public RulesPropagationManager(final VismoRulesEngine engine, final int serverPort) throws IOException {
        this.factory = new ClassPathRulesFactory(PassThroughRule.class.getPackage(), engine);
        this.deliverer = new MessageDeliverer(this, factory);
        this.pid = getRandomID();

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
        messageWatchdog.setManager(this);

        inputQueue = new MessageQueue();
        outQueue = new MessageQueue();
        delQueue = new MessageQueue();

        inputQueue.addObserver(dispatcher);
        outQueue.addObserver(messageSender);
        delQueue.addObserver(deliverer);

        elector = new Elector(this);
    }


    /**
     * @return resolver
     */
    public ClusterRulesResolver getClusterRulesResolver() {
        return clusterRulesResolver;
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
    public int getRandomID() {
        return randomGenerator.nextInt(100000) + 100000;
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
            timer.cancel();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * returns whether pid is elected
     * 
     * @return whether the node is elected
     */
    public boolean isElected() {
        return elector.isElected();
    }


    /**
     * starting all threads needed for the manager
     */
    public void start() {
        try {
            webServer.withWebAppAt(WebAppBuilder.buildFrom(new RulesManagementResource(this)), "/*");
            webServer.start();
            messageSender.init();
            messageReceiver.init();
            dispatcher.start();
            deliverer.start();
            heartbeatReceiver.init();
            heartbeatSender.init();
            messageWatchdog.scheduleWith(timer);
            elector.scheduleWith(timer);
            clusterRulesResolver.scheduleWith(timer);
        } catch (final Throwable x) {
            throw new RuntimeException(x);
        }
    }
}
