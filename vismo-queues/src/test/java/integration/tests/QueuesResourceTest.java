package integration.tests;

import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.EventHandlerTask;
import gr.ntua.vision.monitoring.notify.Registry;
import gr.ntua.vision.monitoring.queues.QueuesRegistry;
import gr.ntua.vision.monitoring.queues.QueuesResource;
import gr.ntua.vision.monitoring.queues.TopicedQueueBean;
import gr.ntua.vision.monitoring.web.WebAppBuilder;

import java.util.List;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * 
 */
public class QueuesResourceTest extends JerseyResourceTest {
    /***/
    private QueuesRegistry registry;


    /**
     * @see integration.tests.JerseyResourceTest#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        registry = new QueuesRegistry(new Registry() {
            @Override
            public EventHandlerTask register(final String topic, final EventHandler handler) {
                // TODO Auto-generated method stub
                return null;
            }


            @Override
            public EventHandlerTask registerToAll(final EventHandler handler) {
                // TODO Auto-generated method stub
                return null;
            }
        });
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

        assertEquals(4, topics.size());
    }


    /**
     * @throws Exception
     */
    public void testShouldListUserQueues() throws Exception {
        final ClientResponse res = resource().path("my-queue").path("reads").post(ClientResponse.class);

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
    public void testShouldRejectInvalidTopicRequests() throws Exception {
        final String TOPIC = "my-topic";
        final ClientResponse res = resource().path("my-queue").path(TOPIC).post(ClientResponse.class);

        assertEquals(ClientResponse.Status.BAD_REQUEST, res.getClientResponseStatus());
    }


    /**
     * @see integration.tests.JerseyResourceTest#resource()
     */
    @Override
    protected WebResource resource() {
        return root().path("queues");
    }
}
