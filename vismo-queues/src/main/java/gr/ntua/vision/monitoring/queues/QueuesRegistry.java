package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * This is used to handle the registration/read/deregistration of a CDMI notification queue. This is wrapper over a
 * {@link Registry}.
 */
public class QueuesRegistry {
    /**
     * The handler that passes events down to a queue.
     */
    private static class TopicQueueHandler implements EventHandler {
        /***/
        private final CDMINotificationQueue q;


        /**
         * Constructor.
         * 
         * @param q
         */
        public TopicQueueHandler(final CDMINotificationQueue q) {
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
    private static final List<String>         AVAILABLE_TOPICS = Arrays.asList("reads", "writes", "topics", "storlets", "*");
    /** the list of registered queues. */
    private final List<CDMINotificationQueue> queuesList;
    /** the actual registry. */
    private final Registry                    registry;


    /**
     * Constructor.
     * 
     * @param registry
     */
    public QueuesRegistry(final Registry registry) {
        this.registry = registry;
        this.queuesList = new ArrayList<CDMINotificationQueue>();
    }


    /**
     * Return the JSON representation of the list. NOTE that this is a workaround, since the {@link MonitoringEvent}s have not a
     * definite schema.
     * 
     * @param queueName
     *            the name of queue to retrieve events from.
     * @return the string json representation of events in the queues.
     */
    public String eventsToJSONString(final String queueName) {
        return toJSONString(getEvents(queueName));
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
        for (final CDMINotificationQueue q : queuesList)
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
     * @return a list of queues.
     */
    public List<CDMINotificationQueue> list() {
        return new CopyOnWriteArrayList<CDMINotificationQueue>(queuesList);
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
     * @throws QueuesRegistrationException
     *             when the topic is not available or a queue with the same name already exists.
     */
    public CDMINotificationQueue register(final String queueName, final String topic) throws QueuesRegistrationException {
        requireAvailabe(topic);

        final CDMINotificationQueue q = new CDMINotificationQueue(queueName, topic);

        if (queuesList.contains(q))
            throw new QueuesRegistrationException("queue already exists: " + queueName);

        registry.register(topic, new TopicQueueHandler(q));
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
     * @throws QueuesRegistrationException
     */
    private static void requireAvailabe(final String topic) throws QueuesRegistrationException {
        if (!isAvailableTopic(topic))
            throw new QueuesRegistrationException("topic not available or invalid: " + topic);
    }


    /**
     * Return the JSON representation of the list. NOTE that this is a workaround, since the {@link MonitoringEvent}s have
     * 
     * @param list
     *            the list of events.
     * @return a json array as well formated json a string.
     */
    private static String toJSONString(final List<MonitoringEvent> list) {
        final StringBuilder buf = new StringBuilder();

        buf.append("[");

        for (int i = 0; i < list.size(); ++i) {
            buf.append(list.get(i).serialize());

            if (i < list.size() - 1)
                buf.append(",");
        }

        buf.append("]");

        return buf.toString();
    }
}
