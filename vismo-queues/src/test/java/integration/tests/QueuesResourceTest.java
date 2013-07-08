package integration.tests;

import gr.ntua.vision.monitoring.queues.QueuesRegistry;
import gr.ntua.vision.monitoring.queues.QueuesResource;
import gr.ntua.vision.monitoring.queues.TopicedQueueBean;
import gr.ntua.vision.monitoring.web.WebAppBuilder;

import java.util.List;

import javax.ws.rs.core.MediaType;

import unit.tests.InMemoryEventRegistry;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * 
 */
public class QueuesResourceTest extends JerseyResourceTest {
    /***/
    private InMemoryEventRegistry eventGenerator;
    /***/
    private QueuesRegistry        registry;


    /**
     * @see integration.tests.JerseyResourceTest#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        eventGenerator = new InMemoryEventRegistry();
        registry = new QueuesRegistry(eventGenerator);
        configureServer(WebAppBuilder.buildFrom(new QueuesResource(registry)), "/*");
        startServer();
    }


    /**
     * @throws Exception
     */
    public void testShouldListAvailableTopics() throws Exception {
        final ClientResponse res = resource().path("topics").accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());

        @SuppressWarnings("unchecked")
        final List<String> topics = res.getEntity(List.class);

        assertEquals(5, topics.size());
    }


    /**
     * @throws Exception
     */
    public void testShouldListUserQueues() throws Exception {
        final ClientResponse res = createQueue("my-queue", "reads");

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());

        final ClientResponse res1 = resource().get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res1.getClientResponseStatus());

        @SuppressWarnings("unchecked")
        final List<TopicedQueueBean> queues = res1.getEntity(List.class);

        assertEquals(1, queues.size());
    }


    /**
     * @throws Exception
     */
    public void testShouldReceiveTopicedEvents() throws Exception {
        final ClientResponse res = createQueue("my-queue", "reads");

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        eventGenerator.pushEvents(10);

        final ClientResponse res1 = resource().path("my-queue").get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res1.getClientResponseStatus());

        final String s = res1.getEntity(String.class);

        System.out.println(s);
    }


    /**
     * @throws Exception
     */
    public void testShouldRejectInvalidTopicRequests() throws Exception {
        final String TOPIC = "my-topic";
        final ClientResponse res = createQueue("my-queue", TOPIC);

        assertEquals(ClientResponse.Status.BAD_REQUEST, res.getClientResponseStatus());
    }


    /**
     * @see integration.tests.JerseyResourceTest#resource()
     */
    @Override
    protected WebResource resource() {
        return root().path("queues");
    }


    /**
     * Create a queue.
     * 
     * @param topic
     * @param queueName
     * @return the client's response.
     */
    private ClientResponse createQueue(final String queueName, final String topic) {
        return resource().path(queueName).path(topic).put(ClientResponse.class);
    }
}
