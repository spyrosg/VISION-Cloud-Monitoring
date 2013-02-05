package gr.ntua.vision.monitoring.web;

import java.util.HashSet;

import javax.ws.rs.core.Application;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
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
    /***/
    private final ContextHandlerCollection coll       = new ContextHandlerCollection();
    /** the actual server. */
    private final Server                   server;
    /** the set of resource to serve. */
    private final HashSet<Object>          singletons = new HashSet<Object>();


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
     * @throws Exception
     * @see org.eclipse.jetty.util.component.AbstractLifeCycle#start()
     */
    public void start() throws Exception {
        buildWebApp();
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
     * @param o
     * @return <code>this</code>.
     */
    public WebServer withResource(final Object o) {
        singletons.add(o);

        return this;
    }


    /**
     * Map and serve the given resource path onto <code>pathSpec</code>.
     * 
     * @param resPath
     *            the resource.
     * @param pathSpec
     *            the path-spec.
     * @return <code>this</code>.
     */
    public WebServer withStaticResourceTo(final Resource resPath, final String pathSpec) {
        coll.addHandler(staticResourcesHandler(resPath, pathSpec));

        return this;
    }


    /**
     * Collect all resources an make an {@link Application} object, served by a servlet from the server's root.
     */
    private void buildWebApp() {
        if (singletons.isEmpty())
            throw new IllegalStateException("you should specify at least one resource");

        coll.addHandler(jerseyResourcesHandler());
        setServerHandlers();
    }


    /**
     * @return a {@link ServletContextHandler} for jersey resources.
     */
    private ServletContextHandler jerseyResourcesHandler() {
        final ServletContextHandler ctxHandler = new ServletContextHandler();
        final ResourceConfig rc = new DefaultResourceConfig();

        rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, new LoggingFilter());
        rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, new LoggingFilter());

        rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        rc.getSingletons().addAll(singletons);
        ctxHandler.addServlet(new ServletHolder(new ServletContainer(rc)), "/*");

        return ctxHandler;
    }


    /**
     * 
     */
    private void setServerHandlers() {
        server.setHandler(coll);
    }


    /**
     * @param pathSpec
     * @param resPath
     * @return a {@link ServletContextHandler} for static resources.
     */
    private static ServletContextHandler staticResourcesHandler(final Resource resPath, final String pathSpec) {
        final ServletContextHandler ctxHandler = new ServletContextHandler();
        final ServletHandler handler = new ServletHandler();

        handler.addServletWithMapping(new ServletHolder(new DefaultServlet()), "/*");
        ctxHandler.setServletHandler(handler);
        ctxHandler.setContextPath(pathSpec);
        ctxHandler.setBaseResource(resPath);

        return ctxHandler;
    }
}
