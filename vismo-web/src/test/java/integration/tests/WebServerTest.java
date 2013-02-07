package integration.tests;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.web.WebServer;

import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.util.resource.Resource;
import org.junit.After;
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


    /**
     * @throws Exception
     */
    @Test
    public void shouldAccessJerseyResources() throws Exception {
        server.withResource(new FooResource("foo-value")).withResource(new BarResource("bar-value"));
        server.start();

        shouldRetrieveResourceWithValue("foo/0", "foo-value");
        shouldRetrieveResourceWithValue("bar", "bar-value");

        assertEquals(ClientResponse.Status.NO_CONTENT,
                     root().path("foo/other-foo").accept(MediaType.TEXT_PLAIN).put(ClientResponse.class)
                             .getClientResponseStatus());

        shouldRetrieveResourceWithValue("foo/1", "other-foo");
    }


    /**
     * @throws Exception
     */
    @Test
    public void shouldAccessStaticResource() throws Exception {
        server.withResource(new BarResource("bar-value"));
        server.withStaticResourceTo(Resource.newClassPathResource("/static"), "/static-foo");
        server.start();

        shouldRetrieveResourceWithValue("bar", "bar-value");
        shouldRetrieveResourceWithValue("static-foo/index.html", "<html>\nfoo\n</html>");
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


    /**
     * @param path
     * @param expectedValue
     */
    private void shouldRetrieveResourceWithValue(final String path, final String expectedValue) {
        final ClientResponse res = root().path(path).accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        assertEquals(expectedValue, res.getEntity(String.class));
    }
}
