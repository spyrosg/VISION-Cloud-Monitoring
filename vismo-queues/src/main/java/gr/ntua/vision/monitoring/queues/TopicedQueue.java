package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;


/**
 *
 */
public class TopicedQueue {
    /***/
    public final String                                name;
    /***/
    public final String                                topic;
    /***/
    private final LinkedBlockingQueue<MonitoringEvent> queue;


    /**
     * Constructor.
     * 
     * @param name
     * @param topic
     */
    public TopicedQueue(final String name, final String topic) {
        this.name = name;
        this.topic = topic;
        this.queue = new LinkedBlockingQueue<MonitoringEvent>(100); // FIXME
    }


    /**
     * Add another event in the queue. If there's no more room, first remove and discard the oldest inserted element.
     * 
     * @param e
     *            the event.
     * @see java.util.AbstractQueue#add(java.lang.Object)
     */
    public void add(final MonitoringEvent e) {
        if (queue.offer(e))
            return;

        queue.remove();
        queue.add(e);
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
        final TopicedQueue other = (TopicedQueue) obj;
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
    public List<MonitoringEvent> removeEvents() {
        final CopyOnWriteArrayList<MonitoringEvent> copy = new CopyOnWriteArrayList<MonitoringEvent>();

        queue.drainTo(copy);

        return copy;
    }


    /**
     * @param q
     * @return the corresponding {@link TopicedQueueBean}.
     */
    public static TopicedQueueBean toBean(final TopicedQueue q) {
        final TopicedQueueBean bean = new TopicedQueueBean();

        bean.setName(q.name);
        bean.setTopic(q.topic);

        return bean;
    }
}
