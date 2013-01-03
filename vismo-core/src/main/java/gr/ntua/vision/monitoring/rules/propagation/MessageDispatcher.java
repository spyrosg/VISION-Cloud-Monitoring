package gr.ntua.vision.monitoring.rules.propagation;

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
    private final static Logger     log = LoggerFactory.getLogger(RulesPropagationManager.class);
    /***/
    private RulesPropagationManager manager;


    @Override
    public void run() {
        Message incomingMessage;
        while (true)
            if (!manager.getInQueue().isQEmpty()) {
                incomingMessage = manager.getInQueue().getMessage();
                // log.info(manager.getPid()+": messageReceived!"+manager.getMessageCounter().getMessageValue(incomingMessage));
                // update message timestamp
                manager.getMessageTimestamp().updateMessageTimestamp(incomingMessage);

                if (!manager.getMessageCounter().contains(incomingMessage)) {
                    manager.getMessageCounter().increaseMessageCount(incomingMessage);
                    manager.getOutQueue().addMessage(incomingMessage);
                } else {
                    manager.getMessageCounter().increaseMessageCount(incomingMessage);
                    if (manager.getMessageCounter().getMessageValue(incomingMessage) == incomingMessage.getGroupSize())
                        // delete message counter & timestamp
                        // manager.getMessageCounter().remove(incomingMessage);
                        // manager.getMessageTimestamp().remove(incomingMessage);
                        // deliver message
                        manager.getDelQueue().addMessage(incomingMessage);
                }
            } else
                synchronized (this) {
                    try {
                        wait();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
    }


    /**
     * @param manager
     */
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
    }


    @Override
    public void update(final Observable o, final Object m) {
        synchronized (this) {
            notify();
        }
    }
}
