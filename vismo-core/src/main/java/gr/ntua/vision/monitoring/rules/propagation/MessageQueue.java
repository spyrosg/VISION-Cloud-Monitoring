package gr.ntua.vision.monitoring.rules.propagation;

import java.util.Collections;
import java.util.Observable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class MessageQueue extends Observable {
    /***/
    final static Logger           log = LoggerFactory.getLogger(MessageQueue.class);

    /**
     * the queue messages saves the messages exchanged from the protocol implementation.
     */
    private final Vector<Message> messages;


    /**
     * initializes the queue
     */
    public MessageQueue() {
        messages = new Vector<Message>();
    }


    /**
     * This method notifies all interested parties that a new message has been added. In our case, the inQueue notifies the
     * Dispatcher and the outQueue notifies the sender. This binding of queues to threads takes place in the initialization of the
     * monitor.
     * 
     * @param m
     */
    public synchronized void addMessage(final Message m) {
        messages.add(m);
        setChanged();
        notifyObservers();
    }


    /**
     * 
     */
    public synchronized void clear() {
        messages.clear();
    }


    /**
     * @return the next message to be delivered.
     */
    public synchronized Message getMessage() {
        if (!messages.isEmpty())
            return messages.remove(0);
        return null;
    }


    /**
     * @return true or false
     */
    public synchronized int getSize() {
        return messages.size();
    }


    /**
     * @return true or false
     */
    public synchronized boolean isQEmpty() {
        return messages.isEmpty();
    }


    /**
     * this method shuffles the queue. Implemented for testing purposes.
     */
    public synchronized void shuffle() {
        Collections.shuffle(messages);
        MessageQueue.log.info("Shuffling Messages, simulating network jitter");
    }


    /**
     * trim the size of the queue
     */
    public synchronized void trimSize() {
        messages.trimToSize();
    }
}
