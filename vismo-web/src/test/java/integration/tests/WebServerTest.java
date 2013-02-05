package integration.tests;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.web.WebServer;

import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.util.resource.Resource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * 
 */
public class WebServerTest {
    /***/
    private static final int    PORT     = 9998;
    /***/
    private static final String ROOT_URL = "http://localhost:" + PORT;
    /***/
    private final Client        client   = new Client();
    /***/
    private final WebServer     server   = new WebServer(PORT);


    /***/
    @Test
    public void getBarResource() {
        final ClientResponse res = root().path("bar").accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        assertEquals("bar", res.getEntity(String.class));
    }


    /***/
    @Test
    public void getFooResource() {
        final ClientResponse res = root().path("foo").accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        assertEquals("foo", res.getEntity(String.class));
    }


    /***/
    @Test
    public void getStaticResource() {
        final ClientResponse res = root().path("/static-foo").path("index.html").accept(MediaType.TEXT_PLAIN)
                .get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        assertEquals("foo\n", res.getEntity(String.class));
    }


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        server.withResource(new FooResource("foo")).withResource(new BarResource("bar"))
                .withStaticResourceTo(Resource.newClassPathResource("/static"), "/static-foo");

        server.start();
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
