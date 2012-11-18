package gr.ntua.vision.monitoring.web;

import gr.ntua.vision.monitoring.notify.EventRegistry;

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


    /**
     * Constructor.
     */
    private VismoService() {
        super("vismo-web");
        serveResourcesFrom(STATIC_RESOURCES_PATH);
    }


    /**
     * @see com.yammer.dropwizard.AbstractService#initialize(com.yammer.dropwizard.config.Configuration,
     *      com.yammer.dropwizard.config.Environment)
     */
    @Override
    protected void initialize(final VismoServiceConfiguration conf, final Environment env) throws Exception {
        final String rootPath = conf.getHttpConfiguration().getRootPath();
        final String cleanedPath = rootPath.substring(0, rootPath.length() - 2);

        env.addServlet(new VisionEventsServlet(new EventRegistry(conf.getAddress())), cleanedPath + "/events/*");
    }


    /**
     * @param resourcePath
     */
    private void serveResourcesFrom(final String resourcePath) {
        addBundle(new AssetsBundle(resourcePath, CacheBuilderSpec.disableCaching(), "/"));
    }


    /**
     * @param args
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        new VismoService().run(args);
    }
}
