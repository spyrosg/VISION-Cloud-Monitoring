package endtoend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventHandler;
import gr.ntua.vision.monitoring.events.EventRegistry;

import org.junit.After;
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
         * @see gr.ntua.vision.monitoring.events.EventHandler#handle(gr.ntua.vision.monitoring.events.Event)
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
    private static final ZContext         ctx                    = new ZContext();
    /***/
    private static final String           eventsDistributionPort = "tcp://127.0.0.1:27890";
    /***/
    private static final String[]         topics                 = new String[] { "A", "B" };
    /***/
    private final TopicAssertionHandler[] handlers               = new TopicAssertionHandler[topics.length];
    /***/
    private final FakeMonitoringInstance  inst                   = new FakeMonitoringInstance(ctx, eventsDistributionPort, 10,
                                                                         topics);
    /***/
    private final EventRegistry           registry               = new EventRegistry(ctx, eventsDistributionPort);


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


    /**
     * 
     */
    @After
    public void tearDown() {
        for (final TopicAssertionHandler handler : handlers)
            handler.hasReceivedEvents();
    }


    /**
     * @throws InterruptedException
     */
    private static void waitForEventsToBeReceived() throws InterruptedException {
        Thread.sleep(2000);
    }
}
