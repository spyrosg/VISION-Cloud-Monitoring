package gr.ntua.vision.monitoring.rules.propagation;

import gr.ntua.vision.monitoring.heartbeat.HeartbeatReceiver;
import gr.ntua.vision.monitoring.heartbeat.HeartbeatSender;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.web.RulesWebServer;

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
    private final MessageReceiver          messageReceiver          = new MessageReceiver();
    /***/
    private final MessageSender            messageSender            = new MessageSender();
    /***/
    private final MessageMap               messageTimestamp         = new MessageMap();
    /***/
    private final RulesPropagationWatchDog messageWatchdog;
    /***/
    private final MessageQueue             outputQueue;
    /***/
    private Integer                        pid;
    /***/
    private final RulesManagementResource  resource                 = new RulesManagementResource();
    /***/
    private final RuleStore                ruleStore;
    /***/
    private int                            size;
    /***/
    private final VismoRulesEngine         vismoRulesEngine;
    /***/
    private final RulesWebServer           webServer;


    /**
     * @param engine
     * @param resourcePath
     * @param serverPort
     * @throws IOException
     */
    public RulesPropagationManager(final VismoRulesEngine engine, final String resourcePath, final int serverPort)
            throws IOException {
        setPid();
        vismoRulesEngine = engine;
        ruleStore = new RuleStore();
        webServer = new RulesWebServer(resourcePath, serverPort);
        heartbeatReceiver = new HeartbeatReceiver(InetAddress.getByName(HEARTBEAT_MULTICAST_IP), HEARTBEAT_MULTICAST_PORT);
        heartbeatSender = new HeartbeatSender(InetAddress.getByName(HEARTBEAT_MULTICAST_IP), HEARTBEAT_MULTICAST_PORT, 1,
                getPid());
        messageWatchdog = new RulesPropagationWatchDog(20000);

        messageReceiver.setManager(this);
        messageSender.setManager(this);
        dispatcher.setManager(this);
        deliverer.setManager(this);
        resource.setManager(this);
        messageWatchdog.setManager(this);

        inputQueue = new MessageQueue();
        outputQueue = new MessageQueue();
        delQueue = new MessageQueue();

        inputQueue.addObserver(dispatcher);
        outputQueue.addObserver(messageSender);
        delQueue.addObserver(deliverer);
    }


    /**
     * @see java.lang.Thread#start()
     */
    @Override
    public synchronized void start() {
        try {
            webServer.start();
            messageSender.init();
            messageReceiver.init();
            dispatcher.start();
            deliverer.start();
            heartbeatReceiver.init();
            heartbeatSender.init();
            messageWatchdog.scheduleWith(new Timer());
        } catch (Throwable x) {
            throw new RuntimeException(x);
        }

        super.start();
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
     * @return the  heart beat receiver
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
        return outputQueue;
    }


    /**
     * returns the unique id of the node
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
     * common place to store rules
     * 
     * @return ruleStore
     */
    public RuleStore getRuleStore() {
        return ruleStore;
    }


    /**
     * @return size.
     */
    public int getSize() {
        return size;
    }


    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            //TODO           
        }
    }


    /***/
    public void setPid() {
        pid = getRandomID();
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
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
