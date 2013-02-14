package endtoend.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;


/**
 * 
 */
public abstract class HTTPClientTest {
    /***/
    private static final Logger log    = LoggerFactory.getLogger(HTTPClientTest.class);
    /***/
    final String                tenant;
    /***/
    private final Client        client = new Client();
    /***/
    private final String        hostURL;
    /***/
    private final String        pass;
    /***/
    private final String        user;


    /**
     * Constructor.
     * 
     * @param hostURL
     * @param tenant
     * @param user
     * @param pass
     */
    public HTTPClientTest(final String hostURL, final String tenant, final String user, final String pass) {
        this.hostURL = hostURL;
        this.tenant = tenant;
        this.user = user;
        this.pass = pass;
    }


    /***/
    @Before
    public void setupHTTPClient() {
        client.addFilter(new HTTPBasicAuthFilter(user + "@" + tenant, pass));
        client.addFilter(new LoggingFilter(System.err));
    }


    /**
     * @return a resource pointing to the entry point of <code>Containers</code>.
     */
    protected WebResource containers() {
        return client.resource("http://" + hostURL + ":8080").path("containers");
    }


    /**
     * @param cont
     */
    protected void createContainer(final String cont) {
        log.debug("creating container {}", cont);

        final ClientResponse res = containers().path(tenant).path(cont).put(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
    }


    /**
     * @param cont
     */
    protected void deleteContainer(final String cont) {
        log.debug("deleting container {}", cont);

        final ClientResponse res = containers().path(tenant).path(cont).delete(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
    }


    /**
     * @param container
     * @param object
     */
    protected void deleteObject(final String container, final String object) {
        log.debug("deleting object {} under {}", object, container);

        final ClientResponse res = forObject(container, object).delete(ClientResponse.class);

        assertEquals(ClientResponse.Status.NO_CONTENT, res.getClientResponseStatus());
    }


    /**
     * @param container
     * @param object
     */
    protected void getObject(final String container, final String object) {
        log.debug("reading back object {} under {}", object, container);

        final ClientResponse res = forObject(container, object).get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
    }


    /**
     * @return a resource pointing to the entry point of <code>Object Service</code>.
     */
    protected WebResource obs() {
        return client.resource("http://" + hostURL).path("vision-cloud").path("object-service");
    }


    /**
     * @param container
     * @param object
     * @param payload
     */
    protected void putObject(final String container, final String object, final String payload) {
        log.debug("creating object {} under {}", object, container);

        final ClientResponse res = forObject(container, object).entity(payload).put(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
    }


    /**
     * @param container
     * @param object
     * @return a {@link Builder}.
     */
    private Builder forObject(final String container, final String object) {
        return obs().path(tenant).path(container).path(object).type("application/cdmi-object").accept("application/cdmi-object")
                .header("X-CDMI-Specification-Version", "1.0");
    }
}
