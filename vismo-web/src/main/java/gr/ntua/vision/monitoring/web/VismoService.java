package gr.ntua.vision.monitoring.web;

import gr.ntua.vision.monitoring.notify.EventRegistry;
import gr.ntua.vision.monitoring.web.resources.HelloWorldResource;

import com.google.common.cache.CacheBuilderSpec;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.bundles.AssetsBundle;
import com.yammer.dropwizard.config.Environment;


/**
 * 
 */
public class VismoService extends Service<VismoServiceConfiguration> {
    /***/
    private static final String STATIC_RESOURCES_PATH = "/static";
    /***/
    private static final String EVENTS_ADDRESS        = "tcp://10.0.3.213:56430";


    /**
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        new VismoService().run(args);
    }


    /**
     * Constructor.
     */
    private VismoService() {
        super("vismo-web");

        serveStaticResourcesFrom(STATIC_RESOURCES_PATH);
    }


    /**
     * @param resourcePath
     */
    private void serveStaticResourcesFrom(final String resourcePath) {
        addBundle(new AssetsBundle(resourcePath, CacheBuilderSpec.disableCaching(), "/"));
    }


    /**
     * @see com.yammer.dropwizard.AbstractService#initialize(com.yammer.dropwizard.config.Configuration,
     *      com.yammer.dropwizard.config.Environment)
     */
    @Override
    protected void initialize(VismoServiceConfiguration conf, Environment env) throws Exception {
        final String rootPath = conf.getHttpConfiguration().getRootPath();
        final String cleanedPath = rootPath.substring(0, rootPath.length() - 2);

        env.addResource(new HelloWorldResource());
        env.addServlet(new VisionEventsServlet(new EventRegistry(EVENTS_ADDRESS, true)), cleanedPath + "/events/*");
    }
}
