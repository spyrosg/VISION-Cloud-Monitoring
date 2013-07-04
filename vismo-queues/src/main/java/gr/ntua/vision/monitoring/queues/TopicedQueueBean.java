package gr.ntua.vision.monitoring.queues;

/**
 *
 */
public class TopicedQueueBean {
    /***/
    private String name;
    /***/
    private String topic;


    /**
     * Constructor.
     */
    public TopicedQueueBean() {
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }


    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }


    /**
     * @param topic
     *            the topic to set
     */
    public void setTopic(final String topic) {
        this.topic = topic;
    }

}
