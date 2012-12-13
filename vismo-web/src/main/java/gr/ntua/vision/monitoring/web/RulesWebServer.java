package gr.ntua.vision.monitoring.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;


/**
 * @author tmessini
 */
public class RulesWebServer {
    /***/
    private static SelectorThread     grizzlyInstance = null;
    /***/
    private static final Logger       log             = LoggerFactory.getLogger(RulesWebServer.class);
    /***/
    private final String              BASE_URI        = RulesWebServer.getBaseURI();
    /***/
    private final Map<String, String> initParams      = new HashMap<String, String>();


    /**
     * @return baseUri
     */
    private static String getBaseURI() {
        return "http://localhost:" + (System.getenv("PORT") != null ? System.getenv("PORT") : "9998") + "/";
    }


    /**
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public void start() throws IllegalArgumentException, IOException {
        initParams.put("com.sun.jersey.config.property.packages", "gr.ntua.vision.monitoring.web.resources");
        RulesWebServer.log.info("starting grizzly...");
        getGrizzly();
        // RulesEventReceiver rulesReceiver = new RulesEventReceiver(multicastAddress, multicastPort);
        // RulesEventSender rulesSender = new RulesEventSender(multicastAddress, multicastPort, timeToLive);
        RulesWebServer.log.info("grizzly started");
    }


    /**
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public void stop() throws IllegalArgumentException, IOException {
        RulesWebServer.log.info("stopping grizzly...");
        getGrizzly().stopEndpoint();
        RulesWebServer.log.info("grizzly stopped");

    }


    /**
     * @return selectorThread.
     * @throws IllegalArgumentException
     * @throws IOException
     */
    private SelectorThread getGrizzly() throws IllegalArgumentException, IOException {
        if (RulesWebServer.grizzlyInstance == null)
            RulesWebServer.grizzlyInstance = GrizzlyWebContainerFactory.create(BASE_URI, initParams);
        return RulesWebServer.grizzlyInstance;
    }

}
