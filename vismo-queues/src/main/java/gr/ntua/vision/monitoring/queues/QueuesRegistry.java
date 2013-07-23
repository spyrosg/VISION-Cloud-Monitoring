package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to handle the registration/read/deregistration of a CDMI notification queue. This is wrapper over a
 * {@link Registry}.
 */
public class QueuesRegistry {
    // TODO: handle more than one queue for the same topic.

    /** the available topics. */
    private static final String[]                  AVAILABLE_TOPICS = { "reads", "writes", "storlets", "*" };
    /***/
    private static final Logger                    log              = LoggerFactory.getLogger(QueuesRegistry.class);
    /** reference to the event handlers. */
    private final ArrayList<CDMIQueueEventHandler> handlers;
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
        this.handlers = new ArrayList<CDMIQueueEventHandler>();
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
    public String[] listAvailableTopics() {
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

        final CDMIQueueEventHandler handler;

        log.debug("registering '" + topic + "' queue");

        if (topic.equals("reads")) {
            handler = new ObsGETEventHandler(q);
            registry.registerToAll(handler);
        } else if (topic.equals("writes")) {
            handler = new ObsPUTEventHandler(q);
            registry.registerToAll(handler);
        } else if (topic.equals("storlets")) {
            handler = new StortletsEventHandler(q);
            registry.register(topic, handler);
        } else {
            handler = new MatchAllEventHandler(q);
            registry.registerToAll(handler);
        }

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
