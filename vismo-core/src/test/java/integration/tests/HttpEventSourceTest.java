package integration.tests;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.sources.HttpEventResource;
import gr.ntua.vision.monitoring.web.WebAppBuilder;
import gr.ntua.vision.monitoring.web.WebServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
    /*
     * TODO: make sure a {@link VismoRulesEngine} object receives posted events.
     */
    /***/
    private static final int    PORT     = 9998;
    /***/
    private static final String ROOT_URL = "http://localhost:" + PORT;
    /***/
    private final Client        client;
    /***/
    private WebServer           server;

    {
        final ClientConfig cc = new DefaultClientConfig();

        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        client = Client.create(cc);
    }


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        final WebAppBuilder builder = new WebAppBuilder();
        final Application application = builder.addResource(new HttpEventResource()).build();

        server = new WebServer(PORT).withWebAppAt(application, "/*");
        server.start();
    }


    /**
     * @throws UnknownHostException
     */
    @Test
    public void shouldAcceptEventsThroughPut() throws UnknownHostException {
        final HashMap<String, Object> mapev = new HashMap<String, Object>();
        mapev.put("timestamp", 123456789L);
        mapev.put("originating-service", "service");
        mapev.put("originating-machine", InetAddress.getLocalHost().getHostAddress());
        mapev.put("topic", "new event");

        final String objRepr = JSONObject.toJSONString(mapev);
        final ClientResponse res = root().path("events").accept(MediaType.APPLICATION_JSON).entity(objRepr)
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
    }


    /**
     * @return a web resource pointing to the server's root.
     */
    private WebResource root() {
        return client.resource(ROOT_URL);
    }
}
