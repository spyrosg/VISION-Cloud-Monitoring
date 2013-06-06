package integration.tests;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.InMemoryEventSink;
import gr.ntua.vision.monitoring.sources.HttpEventResource;
import gr.ntua.vision.monitoring.web.WebAppBuilder;
import gr.ntua.vision.monitoring.web.WebServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test; 

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;


/**
 * 
 */
public class HttpEventSourceTest {
    /***/
    private static final int           PORT     = 9998;
    /***/
    private static final String        ROOT_URL = "http://localhost:" + PORT;
    /***/
    private final Client               client;
    /***/
    private VismoRulesEngine           engine;
    /***/
    private WebServer                  server;
    /***/
    private ArrayList<MonitoringEvent> sink;
    

    {
        final ClientConfig cc = new DefaultClientConfig();

        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        client = Client.create(cc);
    }


    /**
     * @throws UnknownHostException
     */
    @Test
    public void rulesEngineShouldReceivePostedEvent() throws UnknownHostException {
        final String eventRepr = getDefaultEvent();
        final ClientResponse res = root().path("events").accept(MediaType.APPLICATION_JSON).entity(eventRepr)
                .put(ClientResponse.class);
        
        final EventFactory factory = new VismoEventFactory();
        final HttpEventResource httpres = new HttpEventResource(factory);
        final MonitoringEvent e = factory.createEvent(eventRepr);
        httpres.addEventToVismoRulesEngine(sink, engine, e);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
        assertEquals("engine should have received at least one event", 1, sink.size());   
    }


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        final EventFactory factory = new VismoEventFactory();
        final Application application = WebAppBuilder.buildFrom(new HttpEventResource(factory));
        
        server = new WebServer(PORT).withWebAppAt(application, "/*");
        server.start();
        engine = new VismoRulesEngine();
        engine.appendSink(new InMemoryEventSink(sink = new ArrayList<MonitoringEvent>()));
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
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (server != null)
            server.stop();

        if (engine != null)
            engine.halt();
    }


    /**
     * @return a web resource pointing to the server's root.
     */
    private WebResource root() {
        return client.resource(ROOT_URL);
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
