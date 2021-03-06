package gr.ntua.vision.monitoring.resources;

import gr.ntua.vision.monitoring.VismoVMInfo;

import java.util.ArrayList;

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
     */
    public void notifyDeletion(final boolean isInterchange, final String id) {
        if (isInterchange)
            return;

        for (final String host : knownHosts)
            try {
                final ClientResponse res = client.resource("http://" + host).path("rules").path(id)
                        .header(RulesResource.X_VISION_INTERCHANGE_HEADER, "true").delete(ClientResponse.class);

                log.debug("posting to {} => {}", host, res.getClientResponseStatus());
            } catch (final ClientHandlerException e) {
                log.error("posting to {} failed, reason: {}", host, e.getCause());
            }
    }


    /**
     * Push the new rule to all other known nodes. We do this here, in the controller layer, since there isn't a good or general
     * enough rules representation in the domain. VismoRulesEngine knows only of VismoRule instances.
     * 
     * @param isInterchange
     * @param id
     * @param bean
     */
    public void notifyInsertion(final boolean isInterchange, final String id, final RuleBean bean) {
        if (isInterchange)
            return;
        if (!(bean instanceof ThresholdRuleBean))
            return;

        ((ThresholdRuleBean) bean).setId(id);

        for (final String host : knownHosts)
            try {
                final ClientResponse res = client.resource("http://" + host).path("rules")
                        .header(RulesResource.X_VISION_INTERCHANGE_HEADER, "true").type(MediaType.APPLICATION_JSON).entity(bean)
                        .post(ClientResponse.class);

                log.debug("posting to {} => {}", host, res.getClientResponseStatus());
            } catch (final ClientHandlerException e) {
                log.error("posting to {} failed, reason: {}", host, e.getCause());
            }
    }


    /**
     * @param isInterchange
     * @param id
     * @param fieldName
     * @param value
     */
    public void notifyUpdate(final boolean isInterchange, final String id, final String fieldName, final String value) {
        if (isInterchange)
            return;

        for (final String host : knownHosts)
            try {
                final ClientResponse res = client.resource("http://" + host).path("rules").path(id).path(fieldName).path(value)
                        .header(RulesResource.X_VISION_INTERCHANGE_HEADER, "true").put(ClientResponse.class);

                log.debug("posting to {} => {}", host, res.getClientResponseStatus());
            } catch (final ClientHandlerException e) {
                log.error("posting to {} failed, reason: {}", host, e.getCause());
            }
    }


    /**
     * @return the list of known vismo hosts as specified in the jvm's environment.
     */
    private String[] getKnownVismoHosts() {
        final String env = getFromEnv();

        log.trace("known hosts string => {}", env);

        if (env == null || env.isEmpty())
            return new String[0];

        final VismoVMInfo vminfo = new VismoVMInfo();
        final String[] fs = env.split(",");
        final ArrayList<String> hosts = new ArrayList<String>(fs.length);

        for (int i = 0; i < fs.length; ++i) {
            if (fs[i].equals(vminfo.getAddress().getHostName() + ":" + defaultPort))
                continue;

            if (hasPort(fs[i]))
                hosts.add(fs[i]);
            else
                hosts.add(fs[i] + ":" + defaultPort);
        }

        log.debug("known hosts: {}", hosts);

        return hosts.toArray(new String[] {});
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
