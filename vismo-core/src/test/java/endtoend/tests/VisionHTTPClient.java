package endtoend.tests;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.rules.RuleBean;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;


/**
 * This is used to perform operation on the VISION Cloud. Mainly, it can create containers and objects. Basic authentication is
 * required.
 */
public class VisionHTTPClient {
    /***/
    private final Client client;
    /***/
    private final String hostURL;


    /**
     * Constructor.
     * 
     * @param hostURL
     * @param tenant
     * @param user
     * @param pass
     */
    public VisionHTTPClient(final String hostURL, final String tenant, final String user, final String pass) {
        this.hostURL = hostURL;
        this.client = setupClient(tenant, user, pass);
    }


    /**
     * @param tenant
     * @param container
     * @param object
     */
    public void deleteObject(final String tenant, final String container, final String object) {
        final ClientResponse res = forObject(tenant, container, object).delete(ClientResponse.class);

        assertEquals(ClientResponse.Status.NO_CONTENT, res.getClientResponseStatus());
    }


    /**
     * @param tenant
     * @param container
     * @param object
     */
    public void getObject(final String tenant, final String container, final String object) {
        final ClientResponse res = forObject(tenant, container, object).get(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());
    }


    /**
     * @param tenant
     * @param container
     * @param object
     * @param payload
     */
    public void putObject(final String tenant, final String container, final String object, final String payload) {
        final ClientResponse res = forObject(tenant, container, object).entity(payload).put(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());
    }


    /**
     * @param ruleId
     */
    public void removeRule(final String ruleId) {
        final ClientResponse res = rules().path(ruleId).delete(ClientResponse.class);

        assertEquals(ClientResponse.Status.NO_CONTENT, res.getClientResponseStatus());

    }


    /**
     * @param bean
     * @return the id of the rule inserted.
     */
    public String sumbitRule(final RuleBean bean) {
        final ClientResponse res = rules().type(MediaType.APPLICATION_JSON).entity(bean).post(ClientResponse.class);

        assertEquals(ClientResponse.Status.CREATED, res.getClientResponseStatus());

        return res.getEntity(String.class);
    }


    /**
     * @return a resource pointing to the entry point of <code>Containers</code>.
     */
    protected WebResource containers() {
        return client.resource("http://" + hostURL + ":8080").path("containers");
    }


    /**
     * @return a resource pointing to the entry point of <code>Object Service</code>.
     */
    protected WebResource obs() {
        return client.resource("http://" + hostURL).path("vision-cloud").path("object-service");
    }


    /**
     * @return a resource pointing to the vismo rules api.
     */
    protected WebResource rules() {
        return client.resource("http://" + hostURL + ":9996").path("rules");
    }


    /**
     * @param tenant
     * @param container
     * @param object
     * @return a {@link Builder}.
     */
    private Builder forObject(final String tenant, final String container, final String object) {
        return obs().path(tenant).path(container).path(object).type("application/cdmi-object").accept("application/cdmi-object")
                .header("X-CDMI-Specification-Version", "1.0");
    }


    /**
     * @param pass
     * @param user
     * @param tenant
     * @return a configured jersey http client.
     */
    private static Client setupClient(final String tenant, final String user, final String pass) {
        final DefaultClientConfig cc = new DefaultClientConfig();

        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);

        final Client c = Client.create(cc);

        c.setConnectTimeout(30000);
        c.setReadTimeout(30000);

        c.addFilter(new HTTPBasicAuthFilter(user + "@" + tenant, pass));
        c.addFilter(new LoggingFilter(System.err));

        return c;
    }
}
