package endtoend.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

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
         * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
         */
        @Override
        public void handle(final MonitoringEvent e) {
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
    private VismoEventRegistry            registry;


    /**
     * @throws Exception
     */
    @Test
    public void registeredHandlerReceiveRespectiveEventTopics() throws Exception {
        for (int i = 0; i < topics.length; ++i) {
            final String topic = topics[i];

            registry.register(topic, handlers[i] = new TopicAssertionHandler(topic));
        }

        Thread.sleep(30); // spin handlers
        inst.sendEvents();
        Thread.sleep(100); // wait for all events to be received

        for (final TopicAssertionHandler handler : handlers)
            handler.hasReceivedEvents();
    }


    /***/
    @Before
    public void setUp() {
        final ZMQFactory socketFactory = new ZMQFactory(new ZContext());

        setupFakeMonitoring(socketFactory);
        setupRegistry(socketFactory);
    }


    /**
     * @param socketFactory
     */
    private void setupFakeMonitoring(final ZMQFactory socketFactory) {
        inst = new FakeMonitoringInstance(socketFactory.newPubSocket(CONSUMERS_PORT), 10, topics);
    }


    /**
     * @param socketFactory
     */
    private void setupRegistry(final ZMQFactory socketFactory) {
        registry = new VismoEventRegistry(socketFactory, CONSUMERS_PORT);
    }
}
