package integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.web.WebServer;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * 
 */
public class RulesResourceTest {
    /***/
    private static final int               PORT     = 9998;
    /***/
    private static final String            ROOT_URL = "http://localhost:" + PORT;
    /***/
    private final HashMap<Integer, String> catalog  = new HashMap<Integer, String>();
    /***/
    private final Client                   client   = new Client();
    /***/
    private WebServer                      server;


    /***/
    @Test
    public void deleteRule() {
        final String RULE_ID = "10";

        root().path("rules").path("foo-rule").path(RULE_ID).path("foo description").put();

        final ClientResponse res = root().path("rules").path(RULE_ID).delete(ClientResponse.class);
        assertEquals(ClientResponse.Status.NO_CONTENT, res.getClientResponseStatus());
    }


    /***/
    @Test
    public void getRule() {
        final String RULE_ID = "10";

        root().path("rules").path("foo-rule").path(RULE_ID).path("foo description").put();

        final ClientResponse res = root().path("rules").path(RULE_ID).get(ClientResponse.class);
        assertEquals(ClientResponse.Status.NO_CONTENT, res.getClientResponseStatus());
    }


    /***/
    @Test
    public void putRule() {
        final ClientResponse res = root().path("rules").path("foo-rule").path("10").path("foo-desc").put(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
        assertTrue(catalog.containsKey(1));
    }


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        server = new WebServer(PORT);
        server.withResource(new RulesResource(catalog));
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
