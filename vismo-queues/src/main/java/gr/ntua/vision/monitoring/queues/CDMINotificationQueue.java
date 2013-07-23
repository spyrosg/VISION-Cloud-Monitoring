package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to collect all events for a specified topic. The queue can set a cap on the number of notifications to hold.
 */
public class CDMINotificationQueue {
    /***/
    private static final Logger                        log = LoggerFactory.getLogger(CDMINotificationQueue.class);
    /** the name of the queue. */
    public final String                                name;
    /** the topic of the queue. */
    public final String                                topic;
    /** the list of available events. */
    private final LinkedBlockingQueue<MonitoringEvent> queue;


    /**
     * Constructor.
     * 
     * @param name
     *            the name of the queue.
     * @param topic
     *            the topic of the queue.
     */
    public CDMINotificationQueue(final String name, final String topic) {
        this.name = name;
        this.topic = topic;
        this.queue = new LinkedBlockingQueue<MonitoringEvent>(100); // FIXME: size
    }


    /**
     * Add another event in the queue. If there's no more room, first remove and discard the oldest inserted element.
     * 
     * @param notification
     *            the event.
     * @see java.util.AbstractQueue#add(java.lang.Object)
     */
    public void add(final MonitoringEvent notification) {
        log.trace("enqueing {}", notification.serialize());

        if (queue.offer(notification))
            return;

        queue.remove();
        queue.add(notification);
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CDMINotificationQueue other = (CDMINotificationQueue) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (topic == null) {
            if (other.topic != null)
                return false;
        } else if (!topic.equals(other.topic))
            return false;
        return true;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        return result;
    }


    /**
     * This is used to remove the available elements of the queue and pass them back to the caller.
     * 
     * @return a list of available elements in the queue.
     */
    public List<MonitoringEvent> removeNotifications() {
        final CopyOnWriteArrayList<MonitoringEvent> copy = new CopyOnWriteArrayList<MonitoringEvent>();

        queue.drainTo(copy);

        return copy;
    }


    /**
     * @param q
     * @return the corresponding {@link CDMIQueueBean}.
     */
    public static CDMIQueueBean toBean(final CDMINotificationQueue q) {
        final CDMIQueueBean bean = new CDMIQueueBean(q.name);

        bean.setTopic(q.topic);

        return bean;
    }
}
