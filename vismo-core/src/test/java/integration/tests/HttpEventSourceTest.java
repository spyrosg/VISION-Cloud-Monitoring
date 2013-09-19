package integration.tests;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.HttpEventResource;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.InMemoryEventSink;
import gr.ntua.vision.monitoring.web.WebAppBuilder;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * 
 */
public class HttpEventSourceTest extends JerseyResourceTest {
    /***/
    private VismoRulesEngine                 engine;
    /***/
    private final ArrayList<MonitoringEvent> sink = new ArrayList<MonitoringEvent>();


    /**
     * @throws UnknownHostException
     */
    public void testRulesEngineShouldReceivePostedEvent() throws UnknownHostException {
        final String eventRepr = getEvent();
        final ClientResponse res = resource().accept(MediaType.APPLICATION_JSON).entity(eventRepr).put(ClientResponse.class);

        assertEquals(ClientResponse.Status.NO_CONTENT, res.getClientResponseStatus());
        assertEquals("engine should have received at least one event", 1, sink.size());
        assertEquals("should accept proper event", "new event", sink.get(0).topic());
        assertEquals("should accept proper event", "127.0.0.1", sink.get(0).originatingIP().getHostAddress());
        assertEquals("should accept proper event", "ohai", sink.get(0).get("bar"));
    }


    /***/
    public void testShouldAcceptEventsThroughPut() {
        final String eventRepr = getEvent();
        final ClientResponse res = resource().accept(MediaType.APPLICATION_JSON).entity(eventRepr).put(ClientResponse.class);

        assertEquals(ClientResponse.Status.NO_CONTENT, res.getClientResponseStatus());
    }


    /**
     * @throws Exception
     */
    public void testShouldRejectEmptyBodyRequest() throws Exception {
        final ClientResponse res = resource().type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN).put(ClientResponse.class);

        assertEquals("server should reject invalid events", ClientResponse.Status.BAD_REQUEST, res.getClientResponseStatus());
        assertEquals("empty event body not allowed", res.getEntity(String.class));
    }


    /***/
    public void testShouldRejectInvalidEvents() {
        final ClientResponse res = resource().entity("{ \"foo\" : 3 }").type(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class);

        assertEquals("server should reject invalid events", ClientResponse.Status.BAD_REQUEST, res.getClientResponseStatus());
    }


    /**
     * @throws Exception
     */
    public void testShouldRejectInvalidJSONRequest() throws Exception {
        final ClientResponse res = resource().entity("{ \"foo\": \"bar\", \"topic\":  }").type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN).put(ClientResponse.class);

        assertEquals("server should reject invalid events", ClientResponse.Status.BAD_REQUEST, res.getClientResponseStatus());
        assertTrue(res.getEntity(String.class).startsWith("invalid json: "));
    }


    /**
     * @see integration.tests.JerseyResourceTest#resource()
     */
    @Override
    protected WebResource resource() {
        return super.resource().path("events");
    }


    /**
     * @see integration.tests.JerseyResourceTest#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        engine = new VismoRulesEngine();

        final HttpEventResource eventSource = new HttpEventResource(engine);
        new PassThroughRule(engine).submit();
        engine.appendSink(new InMemoryEventSink(sink));

        configureServer(WebAppBuilder.buildFrom(eventSource), "/*");
        startServer();
    }


    /**
     * @see integration.tests.JerseyResourceTest#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        if (engine != null)
            engine.halt();

        super.tearDown();
    }


    /**
     * @return the json representation of a {@link MonitoringEvent}.
     */
    private static String getEvent() {
        final HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("originating-service", "service");
        map.put("topic", "new event");

        map.put("foo", 1);
        map.put("bar", "ohai");

        return JSONObject.toJSONString(map);
    }
}
