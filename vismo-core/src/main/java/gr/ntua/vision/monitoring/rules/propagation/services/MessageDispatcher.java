package gr.ntua.vision.monitoring.rules.propagation.services;

import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.rules.propagation.message.Message;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class MessageDispatcher extends Thread implements Observer {

    /***/
    @SuppressWarnings("unused")
    private final static Logger     log = LoggerFactory.getLogger(MessageDispatcher.class);
    /***/
    private RulesPropagationManager manager;
    /***/
    volatile boolean                stopped               = false;


    /**
     * 
     */
    public void halt() {
        stopped = true;
        interrupt();
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        Message incomingMessage;
        while (!stopped)
            if (!manager.getInQueue().isQEmpty()) {
                incomingMessage = manager.getInQueue().getMessage();
                manager.getMessageTimestamp().updateMessageTimestamp(incomingMessage);

                if (!manager.getMessageCounter().contains(incomingMessage)) {
                    manager.getMessageCounter().increaseMessageCount(incomingMessage);
                    manager.getOutQueue().addMessage(incomingMessage);
                } else {
                    manager.getMessageCounter().increaseMessageCount(incomingMessage);
                    if (manager.getMessageCounter().getMessageValue(incomingMessage) == incomingMessage.getGroupSize())
                        manager.getDelQueue().addMessage(incomingMessage);
                }
            } else
                synchronized (this) {
                    try {
                        wait();
                    } catch (final InterruptedException e) {
                        // TODO
                    }
                }
    }


    /**
     * @param manager
     */
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
    }


    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable o, final Object m) {
        synchronized (this) {
            notify();
        }
    }
}