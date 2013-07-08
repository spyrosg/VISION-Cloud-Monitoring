package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
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
    /**
     *
     */
    private static class FooHandler implements EventHandler {
        /***/
        private final TopicedQueue q;


        /**
         * Constructor.
         * 
         * @param q
         */
        public FooHandler(final TopicedQueue q) {
            this.q = q;
        }


        /**
         * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
         */
        @Override
        public void handle(final MonitoringEvent e) {
            q.add(e);
        }
    }
    /** the available topics. */
    private static final List<String> AVAILABLE_TOPICS = Arrays.asList("reads", "writes", "topics", "storlets", "*");
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
     * Get the list of available events in the queue.
     * 
     * @param queueName
     *            the name of queue.
     * @return the list of events in the queue.
     * @throws NoSuchQueueException
     *             when no queue with specified name exists
     */
    public List<MonitoringEvent> getEvents(final String queueName) throws NoSuchQueueException {
        for (final TopicedQueue q : queuesList)
            if (q.name.equals(queueName))
                return q.removeEvents();

        throw new NoSuchQueueException("no such queue available: " + queueName);
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.Registry#halt()
     */
    public void halt() {
        registry.halt();
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
     * Subscribe a new queue for receiving of the given topic.
     * 
     * @param queueName
     *            the name of the queue.
     * @param topic
     *            the topic to subscribe to.
     * @return the queue object that will be receiving events for given topic.
     * @throws QueuesRegistrationError
     *             when the topic is not available or a queue with the same name already exists.
     */
    public TopicedQueue register(final String queueName, final String topic) throws QueuesRegistrationError {
        requireAvailabe(topic);

        final TopicedQueue q = new TopicedQueue(queueName, topic);

        if (queuesList.contains(q))
            throw new QueuesRegistrationError("queue already exists: " + queueName);

        registry.register(topic, new FooHandler(q));
        queuesList.add(q);

        return q;
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


    /**
     * @param topic
     * @throws QueuesRegistrationError
     */
    private static void requireAvailabe(final String topic) throws QueuesRegistrationError {
        if (!isAvailableTopic(topic))
            throw new QueuesRegistrationError("topic not available or invalid: " + topic);
    }
}
