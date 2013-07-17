package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * This is used to handle the registration/read/deregistration of a CDMI notification queue. This is wrapper over a
 * {@link Registry}.
 */
public class QueuesRegistry {
    // TODO: add corresponding handlers for each topic.
    // TODO: handle more than one queue for the same topic.

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
    private static final List<String>              AVAILABLE_TOPICS = Arrays.asList("reads", "writes", "topics", "storlets", "*");
    /** reference to the event handlers. */
    private final ArrayList<TopicQueueHandler>     handlers;
    /***/
    private final JSONParser                       parser           = new JSONParser();
    /** the list of registered queues. */
    private final ArrayList<CDMINotificationQueue> queuesList;
    /** the actual registry. */
    private final Registry                         registry;


    /**
     * Constructor.
     * 
     * @param registry
     */
    public QueuesRegistry(final Registry registry) {
        this.registry = registry;
        this.queuesList = new ArrayList<CDMINotificationQueue>();
        this.handlers = new ArrayList<QueuesRegistry.TopicQueueHandler>();
    }


    /**
     * Get the list of available events in the queue, ready to be consumed by a CDMI read op.
     * 
     * @param queueName
     *            the name of queue.
     * @return the list of events in the queue.
     * @throws NoSuchQueueException
     *             when no queue with specified name exists.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getCDMIEvents(final String queueName) {
        final List<MonitoringEvent> list = getEvents(queueName);
        final ArrayList<Map<String, Object>> values = new ArrayList<Map<String, Object>>(list.size());

        for (final MonitoringEvent e : list) {
            final String s = e.serialize();

            try {
                final Map<String, Object> map = (Map<String, Object>) parser.parse(s);

                values.add(map);
            } catch (final ParseException e1) {
                e1.printStackTrace();
            }
        }

        return values;
    }


    /**
     * Get the list of available events in the queue.
     * 
     * @param queueName
     *            the name of queue.
     * @return the list of events in the queue.
     * @throws NoSuchQueueException
     *             when no queue with specified name exists.
     */
    public List<MonitoringEvent> getEvents(final String queueName) throws NoSuchQueueException {
        for (final CDMINotificationQueue q : queuesList)
            if (q.name.equals(queueName))
                return q.removeNotifications();

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

        final TopicQueueHandler handler = new TopicQueueHandler(q);

        registry.register(topic, handler);
        queuesList.add(q);
        handlers.add(handler);

        return q;
    }


    /**
     * @param queueName
     */
    public void unregister(final String queueName) {
        final CDMINotificationQueue q = new CDMINotificationQueue(queueName, "*");
        final int idx = queuesList.indexOf(q);

        if (idx == -1)
            throw new NoSuchQueueException("no such queue available: " + queueName);

        queuesList.remove(idx);
        registry.unregister(handlers.remove(idx));
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
}
