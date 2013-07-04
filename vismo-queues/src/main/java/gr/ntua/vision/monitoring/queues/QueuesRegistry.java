package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 *
 */
public class QueuesRegistry {
    /** the available topics. */
    private static final List<String> AVAILABLE_TOPICS = Arrays.asList("reads", "writes", "topics", "storlets");
    /** the list of registered queues. */
    private final List<TopicedQueue>  queuesList;
    /** the actual registry. */
    private final Registry            registry;


    /**
     * Constructor.
     * 
     * @param registry
     */
    public QueuesRegistry(final Registry registry) {
        this.registry = registry;
        this.queuesList = new ArrayList<TopicedQueue>();
    }


    /**
     * @return the list of queues.
     */
    public List<TopicedQueue> list() {
        return new CopyOnWriteArrayList<TopicedQueue>(queuesList);
    }


    /**
     * @return the list of available topics.
     */
    @SuppressWarnings("static-method")
    public List<String> listAvailableTopics() {
        return AVAILABLE_TOPICS;
    }


    /**
     * @param topic
     * @param handler
     * @see gr.ntua.vision.monitoring.notify.Registry#register(java.lang.String, gr.ntua.vision.monitoring.notify.EventHandler)
     */
    public void register(final String topic, final EventHandler handler) {
        registry.register(topic, handler);
    }


    /**
     * @param queueName
     * @param topic
     * @return
     */
    public TopicedQueue register(final String queueName, final String topic) {
        requireAvailabe(topic);

        final TopicedQueue q = new TopicedQueue(queueName, topic);

        if (queuesList.contains(q))
            throw new QueuesRegistrationError("queue already exists: " + queueName);

        registry.register(topic, new TODOHandler());
        queuesList.add(q);

        return q;
    }


    /**
     * @param topic
     */
    private void requireAvailabe(final String topic) {
        if (!isAvailableTopic(topic))
            throw new QueuesRegistrationError("topic not available or invalid: " + topic);
    }


    /**
     * Check that given string is one of the available topics.
     * 
     * @param s
     * @return <code>true</code> iff it is an available topic, <code>false</code> otherwise.
     */
    private static boolean isAvailableTopic(final String s) {
        for (final String t : AVAILABLE_TOPICS)
            if (t.equalsIgnoreCase(s))
                return true;

        return false;
    }
}
