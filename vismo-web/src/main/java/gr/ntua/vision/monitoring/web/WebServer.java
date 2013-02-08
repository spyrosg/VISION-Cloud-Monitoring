package gr.ntua.vision.monitoring.web;

import java.util.HashSet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import com.sun.jersey.api.container.filter.LoggingFilter;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.spi.container.servlet.ServletContainer;


/**
 * A simple web server, for serving jersey resources.
 */
public class WebServer {
    /** the set of jersey <em>singleton</em> resources. */
    private final HashSet<Object> resources              = new HashSet<Object>();
    /** the actual server. */
    private final Server          server;
    /***/
    private ServletContextHandler staticResourcesHandler = null;


    /**
     * Constructor.
     * 
     * @param port
     *            the port to listen to.
     */
    public WebServer(final int port) {
        this.server = new Server(port);
    }


    /**
     * This should be called just before start. It readies the various part of the web server.
     * 
     * @param pathSpec
     *            this is used as the path to serve the jersey resources under.
     * @return <code>this</code>.
     */
    public WebServer build(final String pathSpec) {
        final ContextHandlerCollection coll = new ContextHandlerCollection();

        // NOTE: the order in which we specify handlers in the collection matters:
        // it should go from the most specific to the least
        coll.addHandler(getJerseyResourcesHandler(buildAppFrom(resources), pathSpec));

        if (staticResourcesHandler != null)
            coll.addHandler(staticResourcesHandler);

        server.setHandler(coll);

        return this;
    }


    /**
     * @throws Exception
     * @see org.eclipse.jetty.util.component.AbstractLifeCycle#start()
     */
    public void start() throws Exception {
        server.start();
    }


    /**
     * @throws Exception
     * @see org.eclipse.jetty.util.component.AbstractLifeCycle#stop()
     */
    public void stop() throws Exception {
        server.stop();
        server.join();
    }


    /**
     * Add a jersey resource.
     * 
     * @param resource
     *            the resource object.
     * @return <code>this</code>.
     */
    public WebServer withResource(final Object resource) {
        resources.add(resource);

        return this;
    }


    /**
     * @param resourcesPath
     * @param pathSpec
     * @return <code>this</code>.
     */
    public WebServer withStaticResourcesFrom(final String resourcesPath, final String pathSpec) {
        staticResourcesHandler = getStaticResourcesHandler(resourcesPath, pathSpec);

        return this;
    }


    /**
     * Configure a {@link ResourceConfig} with the given set of resources. This is used to configure the dynamic part of the web
     * app.
     * 
     * @param resources
     *            the set of resources.
     * @return a {@link ResourceConfig} object.
     */
    private static ResourceConfig buildAppFrom(final HashSet<Object> resources) {
        if (resources.isEmpty())
            throw new IllegalStateException("you should specify at least one resource");

        final ResourceConfig rc = new DefaultResourceConfig();

        // NOTE: debug help for requests/responses
        rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, new LoggingFilter());
        rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, new LoggingFilter());

        // NOTE: this is used to automagically serialize/deserialize pojos in requests/responses
        rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        rc.getSingletons().addAll(resources);

        return rc;
    }


    /**
     * @param rc
     * @param pathSpec
     * @return a {@link ServletContextHandler}.
     */
    private static ServletContextHandler getJerseyResourcesHandler(final ResourceConfig rc, final String pathSpec) {
        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addServlet(new ServletHolder(new ServletContainer(rc)), pathSpec);

        return context;
    }


    /**
     * @param resourcesPath
     * @param pathSpec
     * @return a {@link ServletContextHandler}.
     */
    private static ServletContextHandler getStaticResourcesHandler(final String resourcesPath, final String pathSpec) {
        final ServletContextHandler ctxHandler = new ServletContextHandler();

        ctxHandler.addServlet(new ServletHolder(new DefaultServlet()), pathSpec);
        ctxHandler.setBaseResource(Resource.newClassPathResource(resourcesPath));

        return ctxHandler;
    }
}
