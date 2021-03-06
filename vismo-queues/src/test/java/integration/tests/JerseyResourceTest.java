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
    private static final int    PORT     = 9998;
    /***/
    private static final String ROOT_URL = "http://localhost:" + PORT;
    /***/
    private Client              client;
    /***/
    private WebServer           server;


    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        server = new WebServer(PORT);

        final ClientConfig cc = new DefaultClientConfig();

        configureClient(cc);

        client = Client.create(cc);
    }


    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        if (server != null)
            server.stop();
    }


    /**
     * @param cc
     */
    @SuppressWarnings("static-method")
    protected void configureClient(final ClientConfig cc) {
        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
    }


    /**
     * @param app
     * @param pathSpec
     */
    protected void configureServer(final Application app, final String pathSpec) {
        if (server != null)
            server.withWebAppAt(app, pathSpec);
    }


    /**
     * @return a handle to the resource.
     */
    protected abstract WebResource resource();


    /**
     * @return a web resource pointing to the server's root.
     */
    protected WebResource root() {
        return client.resource(ROOT_URL);
    }


    /**
     * @throws Exception
     */
    protected void startServer() throws Exception {
        server.start();
    }


    /**
     * @throws Exception
     */
    protected void stopServer() throws Exception {
        server.stop();
    }
}
