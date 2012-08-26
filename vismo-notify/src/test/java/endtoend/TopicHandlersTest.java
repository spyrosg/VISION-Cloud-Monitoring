package endtoend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.EventRegistry;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;


/**
 *
 */
public class TopicHandlersTest {
    /**
     * This is used to assert that handlers only receive events for given topic.
     */
    public static class TopicAssertionHandler implements EventHandler {
        /***/
        private final String expectedTopic;
        /***/
        private boolean      receivedEvents = false;


        /**
         * @param topic
         */
        public TopicAssertionHandler(final String topic) {
            this.expectedTopic = topic;
        }


        /**
         * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.Event)
         */
        @Override
        public void handle(final Event e) {
            receivedEventWithExpectedTopic((String) e.get("topic"));
            receivedEvents = true;
        }


        /***/
        public void hasReceivedEvents() {
            assertTrue("no events of topic " + expectedTopic + " received", receivedEvents);
        }


        /**
         * @param topic
         */
        private void receivedEventWithExpectedTopic(final String topic) {
            assertEquals(expectedTopic, topic);
        }
    }

    /***/
    private static final String           CONSUMERS_PORT = "tcp://127.0.0.1:27890";
    /***/
    private static final String[]         topics         = new String[] { "A", "B" };
    /***/
    private final TopicAssertionHandler[] handlers       = new TopicAssertionHandler[topics.length];
    /***/
    private FakeMonitoringInstance        inst;
    /***/
    private EventRegistry                 registry;


    /**
     * @throws Exception
     */
    @Test
    public void registeredHandlerReceiveRespectiveEventTopics() throws Exception {
        for (int i = 0; i < topics.length; ++i) {
            final String topic = topics[i];

            registry.register(topic, handlers[i] = new TopicAssertionHandler(topic));
        }

        inst.sendEvents();
        waitForEventsToBeReceived();
    }


    /***/
    @Before
    public void setUp() {
        final ZMQSockets zmq = new ZMQSockets(new ZContext());

        setupFakeMonitoring(zmq);
        setupRegistry(zmq);
    }


    /***/
    @After
    public void tearDown() {
        for (final TopicAssertionHandler handler : handlers)
            handler.hasReceivedEvents();
    }


    /**
     * @param zmq
     */
    private void setupFakeMonitoring(final ZMQSockets zmq) {
        inst = new FakeMonitoringInstance(zmq.newBoundPubSocket(CONSUMERS_PORT), 10, topics);
    }


    /**
     * @param zmq
     */
    private void setupRegistry(final ZMQSockets zmq) {
        registry = new EventRegistry(CONSUMERS_PORT, true);
    }


    /**
     * @throws InterruptedException
     */
    private static void waitForEventsToBeReceived() throws InterruptedException {
        Thread.sleep(3000);
    }
}
