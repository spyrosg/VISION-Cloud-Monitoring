package gr.ntua.vision.monitoring.queues;

/**
 *
 */
public class TopicedQueue {
    /***/
    public final String name;
    /***/
    public final String topic;


    /**
     * Constructor.
     * 
     * @param name
     * @param topic
     */
    public TopicedQueue(final String name, final String topic) {
        this.name = name;
        this.topic = topic;
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
