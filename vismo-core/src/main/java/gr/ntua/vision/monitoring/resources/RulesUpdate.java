package gr.ntua.vision.monitoring.resources;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;


/**
 * This is used to update all known vismo hosts with newly submitted/deleted rules. We don't care whether the submission to the
 * other hosts has failed or not. The list of known hosts should be a comma separated string of hosts, and can be set in the
 * environment or as a system property.
 */
public class RulesUpdate {
    /***/
    private static final String ENV_HOSTS      = "VISMO_HOSTS";
    /***/
    private static final Logger log            = LoggerFactory.getLogger(RulesResource.class);
    /***/
    private static final String PROPERTY_HOSTS = "vismo.hosts";
    /***/
    private final Client        client;
    /***/
    private final int           defaultPort;
    /***/
    private final String[]      knownHosts;


    /**
     * Constructor.
     * 
     * @param defaultPort
     *            the port used by all vismo instances to update rules.
     */
    public RulesUpdate(final int defaultPort) {
        this.defaultPort = defaultPort;
        this.client = configureClient();
        this.knownHosts = getKnownVismoHosts();
    }


    /**
     * @param isInterchange
     * @param id
     * @param bean
     */
    public void push(final boolean isInterchange, final String id, final RuleBean bean) {
        if (isInterchange)
            return;
        if (!(bean instanceof ThresholdRuleBean))
            return;

        ((ThresholdRuleBean) bean).setId(id);

        for (final String host : knownHosts)
            try {
                final ClientResponse res = client.resource("http://" + host + "/rules")
                        .header(RulesResource.X_VISION_INTERCHANGE_HEADER, "true").type(MediaType.APPLICATION_JSON).entity(bean)
                        .post(ClientResponse.class);

                log.debug("posting to {} => {}", host, res.getClientResponseStatus());
            } catch (final ClientHandlerException e) {
                log.error("posting to {} failed, reason: {}", host, e.getCause());
            }
    }


    /**
     * @return the list of known vismo hosts.
     */
    private String[] getKnownVismoHosts() {
        final String var = getFromEnv();

        if (var == null || var.isEmpty())
            return new String[0];

        final String[] fs = var.split(",");
        final String[] hosts = new String[fs.length];

        for (int i = 0; i < fs.length; ++i)
            if (hasPort(fs[i]))
                hosts[i] = fs[i];
            else
                hosts[i] = fs[i] + ":" + defaultPort;

        return hosts;
    }


    /**
     * @return a configured jersey client.
     */
    private static Client configureClient() {
        final DefaultClientConfig cc = new DefaultClientConfig();

        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        cc.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 3000);
        cc.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, 3000);

        return Client.create(cc);
    }


    /**
     * @return the vismo hosts env variable or system property. Can be non existent.
     */
    private static String getFromEnv() {
        final String env = System.getenv(ENV_HOSTS);

        if (env != null)
            return env;

        return System.getProperty(PROPERTY_HOSTS);
    }


    /**
     * @param s
     * @return <code>true</code> if the string ends with a port scheme.
     */
    private static boolean hasPort(final String s) {
        final int idx = s.indexOf(":");

        if (idx <= 0 || idx == s.length())
            return false;

        return Character.isDigit(s.charAt(idx + 1));
    }
}
