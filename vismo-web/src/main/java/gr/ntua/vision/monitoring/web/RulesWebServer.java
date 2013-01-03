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
    private static final Logger       log             = LoggerFactory.getLogger(RulesWebServer.class);
    /***/
    private final String              BASE_URI;
    /***/
    private final Map<String, String> initParams      = new HashMap<String, String>();    
    /***/
    private final int               serverPort;
    

    /**
     * we configure the system so as to use the resource package.
     * @param resourcePackage
     * @param serverPort 
     */
    public RulesWebServer(String resourcePackage, int serverPort)
    {
        this.serverPort = serverPort;
        BASE_URI= "http://localhost:"+serverPort+"/";
        initParams.put("com.sun.jersey.config.property.packages", resourcePackage); 
    }
    


    /**
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public void start() throws IllegalArgumentException, IOException {        
        getGrizzly();      
        RulesWebServer.log.info("grizzly started at "+getGrizzlyPort());
    }


    /**
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public void stop() throws IllegalArgumentException, IOException {
        getGrizzly().stopEndpoint();
        RulesWebServer.log.info("grizzly stopped");

    }


    /**
     * @return selectorThread.
     * @throws IllegalArgumentException
     * @throws IOException
     */
    private SelectorThread getGrizzly() throws IllegalArgumentException, IOException {
        return GrizzlyWebContainerFactory.create(BASE_URI, initParams);
    }

    /**
     * @return Grizzly port
     */
    private int getGrizzlyPort(){
        
        return serverPort;
        
    }
    
}
