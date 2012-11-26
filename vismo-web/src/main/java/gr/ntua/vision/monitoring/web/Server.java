package gr.ntua.vision.monitoring.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;


public class Server {

    private static SelectorThread    instance   = null;

    public static final String       BASE_URI   = getBaseURI();

    final static Map<String, String> initParams = new HashMap<String, String>();


    public static void start() throws IllegalArgumentException, IOException {
        initParams.put("com.sun.jersey.config.property.packages", "gr.ntua.vision.monitoring.web.resources");
        System.out.println("Starting grizzly...");
        getGrizzly();
        System.out.println(String.format("grizzly started"));
    }


    public static void stop() throws IllegalArgumentException, IOException {
        System.out.println("Stopping grizzly...");
        getGrizzly().stopEndpoint();
        System.out.println(String.format("grizzly stopped"));

    }


    public static SelectorThread getGrizzly() throws IllegalArgumentException, IOException {
        if (instance == null) {
            instance = GrizzlyWebContainerFactory.create(BASE_URI, initParams);
        }
        return instance;
    }


    private static String getBaseURI() {
        return "http://localhost:" + (System.getenv("PORT") != null ? System.getenv("PORT") : "9998") + "/";
    }

}
