package integration.tests;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.HttpEventResource;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.InMemoryEventSink;
import gr.ntua.vision.monitoring.web.WebAppBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.sun.jersey.api.client.ClientResponse;


/**
 * 
 */
public class HttpEventSourceTest extends JerseyResourceTest {
    /***/
    private VismoRulesEngine                 engine;
    /***/
    private final ArrayList<MonitoringEvent> sink = new ArrayList<MonitoringEvent>();


    /**
     * @see integration.tests.JerseyResourceTest#setUp()
     */
    @Override
    public void setUp() throws Exception {
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
    public void tearDown() throws Exception {
        if (engine != null)
            engine.halt();

        super.tearDown();
    }


    /***/
    public void testRulesEngineShouldReceivePostedEvent() {
        final String eventRepr = getEvent();
        final ClientResponse res = root().path("events").accept(MediaType.APPLICATION_JSON).entity(eventRepr)
                .put(ClientResponse.class);

        assertEquals(ClientResponse.Status.NO_CONTENT, res.getClientResponseStatus());
        assertEquals("engine should have received at least one event", 1, sink.size());
    }


    /***/
    public void testShouldAcceptEventsThroughPut() {
        final String eventRepr = getEvent();
        final ClientResponse res = root().path("events").accept(MediaType.APPLICATION_JSON).entity(eventRepr)
                .put(ClientResponse.class);

        assertEquals(ClientResponse.Status.NO_CONTENT, res.getClientResponseStatus());
    }


    /***/
    public void testShouldRejectInvalidEvents() {
        final ClientResponse res = root().path("events").entity("{ \"foo\" : 3 }").put(ClientResponse.class);

        assertEquals("server should reject invalid events", ClientResponse.Status.BAD_REQUEST, res.getClientResponseStatus());
    }


    /**
     * @return the json representation of a {@link MonitoringEvent}.
     */
    private static String getEvent() {
        final HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("originating-service", "service");
        map.put("topic", "new event");

        return JSONObject.toJSONString(map);
    }
}
