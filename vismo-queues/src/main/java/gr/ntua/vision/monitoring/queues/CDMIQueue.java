package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to collect all events for a specified topic. The queue can set a cap on the number of events to hold in memory.
 */
public class CDMIQueue {
    /***/
    private static final Logger                        log = LoggerFactory.getLogger(CDMIQueue.class);
    /** the name of the queue. */
    public final String                                name;
    /** the list of available events. */
    private final LinkedBlockingQueue<MonitoringEvent> queue;
    /** the associated topic */
    public final String topic;


    /**
     * Constructor.
     * 
     * @param name
     *            the name of the queue.
     * @param size
     */
    public CDMIQueue(final String name, final String topic, final int size) {
        this.name = name;
        this.topic = topic;
        this.queue = new LinkedBlockingQueue<MonitoringEvent>(size);
    }


    /**
     * Add another event in the queue. If there's no more room, first remove and discard the oldest inserted element.
     * 
     * @param event
     *            the event.
     * @see java.util.AbstractQueue#add(java.lang.Object)
     */
    public void add(final MonitoringEvent event) {
        log.trace("enqueing {}", event.serialize());

        if (queue.offer(event))
            return;

        queue.remove();
        queue.add(event);
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
        final CDMIQueue other = (CDMIQueue) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }


    /**
     * @return the elements available in the queue.
     */
    public Collection<MonitoringEvent> getEvents() {
        return Collections.unmodifiableCollection(queue);
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }


    /**
     * Remove the first <code>count</code> elements of the queue.
     * 
     * @param count
     *            the number of elements to remove.
     */
    public void removeEvents(final int count) {
        if (count == Integer.MAX_VALUE)
            queue.clear();
        else
            for (int i = 0; i < count && !queue.isEmpty(); ++i)
                queue.poll();
    }


    /**
     * @param q
     * @return the corresponding {@link CDMIQueueBean}.
     */
    public static CDMIQueueBean toBean(final CDMIQueue q) {
        return new CDMIQueueBean(q.name, q.topic);
    }
}
