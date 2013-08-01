package integration.tests;

import gr.ntua.vision.monitoring.web.WebServer;

import javax.ws.rs.core.Application;

import junit.framework.TestCase;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;


/**
 * 
 */
public abstract class JerseyResourceTest extends TestCase {
    /***/
    private static final int    PORT     = 19998;
    /***/
    private static final String ROOT_URL = "http://localhost:" + PORT;
    /***/
    private final Client        client;
    /***/
    private final WebServer     server   = new WebServer(PORT);

    {
        final ClientConfig cc = new DefaultClientConfig();

        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        client = Client.create(cc);
    }


    /**
     * @param app
     * @param pathSpec
     */
    protected void configureServer(final Application app, final String pathSpec) {
        server.withWebAppAt(app, pathSpec);
    }


    /**
     * @return a web resource pointing to the server's root.
     */
    protected WebResource resource() {
        return client.resource(ROOT_URL);
    }


    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }


    /**
     * @throws Exception
     */
    protected void startServer() throws Exception {
        server.start();
    }


    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        server.stop();
        super.tearDown();
    }
}
