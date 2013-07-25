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


    /***/
    public void testShouldRejectInvalidEvents() {
        final ClientResponse res = resource().entity("{ \"foo\" : 3 }").put(ClientResponse.class);

        assertEquals("server should reject invalid events", ClientResponse.Status.BAD_REQUEST, res.getClientResponseStatus());
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

        final HttpEventResource eventSource = new HttpEventResource();

        engine = new VismoRulesEngine();
        new PassThroughRule(engine).submit();
        engine.appendSink(new InMemoryEventSink(sink));
        eventSource.add(engine);

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
