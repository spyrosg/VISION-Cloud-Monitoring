package gr.ntua.vision.monitoring.resources;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;


/**
 *
 */
public class RulesUpdate {
    /***/
    private static final Logger log        = LoggerFactory.getLogger(RulesResource.class);
    /***/
    private final Client        client;
    /***/
    private final String[]      knownHosts = { "localhost:9997", "localhost:9998" };


    /**
     * Constructor.
     */
    public RulesUpdate() {
        this.client = configureClient();
    }


    /**
     * @param bean
     */
    public void push(final RuleBean bean) {
        for (final String host : knownHosts) {
            final ClientResponse res = client.resource("http://" + host + "/rules")
                    .header(RulesResource.X_INTERCHANGE_HEADER, "true").type(MediaType.APPLICATION_JSON).entity(bean)
                    .post(ClientResponse.class);

            log.debug("posting to {} => {}", host, res.getClientResponseStatus());
        }
    }


    /**
     * @param bean
     */
    public void update(final ThresholdRuleBean bean) {
        // TODO
    }


    /**
     * 
     */
    private void getKnownVismoHosts() {
    }


    /**
     * @return a configured jersey client.
     */
    private static Client configureClient() {
        final DefaultClientConfig cc = new DefaultClientConfig();

        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);

        return Client.create(cc);
    }
}
