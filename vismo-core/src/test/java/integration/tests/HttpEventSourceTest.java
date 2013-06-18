package integration.tests;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.InMemoryEventSink;
import gr.ntua.vision.monitoring.sources.HttpEventResource;
import gr.ntua.vision.monitoring.web.WebAppBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 * 
 */
public class HttpEventSourceTest extends JerseyResourceTest {
    /***/
    private final VismoRulesEngine           engine = new VismoRulesEngine();
    /***/
    private final ArrayList<MonitoringEvent> sink   = new ArrayList<MonitoringEvent>();


    /**
     * @throws UnknownHostException
     */
    public void testRulesEngineShouldReceivePostedEvent() throws UnknownHostException {
        final String eventRepr = getDefaultEvent();
        final ClientResponse res = root().path("events").accept(MediaType.APPLICATION_JSON).entity(eventRepr)
                .put(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals("engine should have received at least one event", 1, sink.size());
    }


    /**
     * @see integration.tests.JerseyResourceTest#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        final HttpEventResource eventSource = new HttpEventResource();

        eventSource.add(engine);
        engine.appendSink(new InMemoryEventSink(sink));
        new PassThroughRule(engine).submit();

        configureServer(WebAppBuilder.buildFrom(eventSource), "/*");
        startServer();
    }


    /**
     * @throws UnknownHostException
     */
    @Test
    public void shouldAcceptEventsThroughPut() throws UnknownHostException {
        final String eventRepr = getDefaultEvent();
        final ClientResponse res = root().path("events").accept(MediaType.APPLICATION_JSON).entity(eventRepr)
                .put(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
    }


    /***/
    @Test
    public void shouldRejectInvalidEvents() {
        final ClientResponse res = root().path("events").entity("{ \"foo\" : 3 }").put(ClientResponse.class);

        assertEquals("server should reject invalid events", ClientResponse.Status.BAD_REQUEST, res.getClientResponseStatus());
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


    /**
     * @return the json representation of a {@link MonitoringEvent}.
     * @throws UnknownHostException
     */
    private static String getDefaultEvent() throws UnknownHostException {
        final HashMap<String, Object> mapev = new HashMap<String, Object>();
        mapev.put("timestamp", System.currentTimeMillis());
        mapev.put("originating-service", "service");
        mapev.put("originating-machine", InetAddress.getLocalHost().getHostAddress());
        mapev.put("topic", "new event");

        return JSONObject.toJSONString(mapev);
    }
}
