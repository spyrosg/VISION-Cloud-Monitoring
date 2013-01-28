package gr.ntua.vision.monitoring.rules.propagation.message;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class MessageMap {
    /***/
    final Logger                                   log = LoggerFactory.getLogger(MessageMap.class);

    /**
     * the queue messages saves the messages exchanged from the protocol implementation.
     */
    private final ConcurrentHashMap<Message, Long> messages;


    /**
     * initializes the queue
     */
    public MessageMap() {
        messages = new ConcurrentHashMap<Message, Long>();
    }


    /**
     * 
     */
    public synchronized void clear() {
        messages.clear();
    }


    /**
     * @param m
     * @return true or false
     */
    public synchronized boolean contains(final Message m) {
        return messages.containsKey(m);
    }


    /**
     * @param m
     * @return count
     */
    public synchronized Long getMessageValue(final Message m) {
        return messages.get(m);
    }


    /**
     * @return true or false
     */
    public synchronized int getSize() {
        return messages.size();
    }


    /**
     * This method notifies all interested parties that a new message has been added. In our case, the inQueue notifies the
     * Dispatcher and the outQueue notifies the sender. This binding of queues to threads takes place in the initialization of the
     * monitor.
     * 
     * @param m
     */
    public synchronized void increaseMessageCount(final Message m) {
        long temp = 0;
        if (messages.get(m) == null)
            messages.put(m, (long) 0);
        else {
            temp = messages.get(m) + 1;
            messages.put(m, temp);
        }
    }


    /**
     * @return true or false
     */
    public synchronized boolean isEmpty() {
        return messages.isEmpty();
    }


    /**
     * @return Set<Messages>
     */
    public Set<Message> keys() {
        return messages.keySet();
    }


    /**
     * @param m
     * @return position
     */
    public synchronized Long remove(final Message m) {
        return messages.remove(m);
    }


    /**
     * @param m
     */
    public synchronized void updateMessageTimestamp(final Message m) {
        messages.put(m, System.currentTimeMillis());

    }

}
