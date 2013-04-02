package gr.ntua.vision.monitoring.web;

import javax.ws.rs.core.Application;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import com.sun.jersey.spi.container.servlet.ServletContainer;


/**
 * A simple web server, for serving jersey resources.
 */
public class WebServer {
    /***/
    private final ContextHandlerCollection coll;
    /** the actual server. */
    private final Server                   server;


    /**
     * Constructor.
     * 
     * @param port
     *            the port to listen to.
     */
    public WebServer(final int port) {
        this.server = new Server(port);
        this.coll = new ContextHandlerCollection();
    }


    /**
     * @throws Exception
     * @see org.eclipse.jetty.util.component.AbstractLifeCycle#start()
     */
    public void start() throws Exception {
        server.setHandler(coll);
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
     * @param resourcesPath
     * @param pathSpec
     * @return <code>this</code>.
     */
    public WebServer withStaticResourcesAt(final String resourcesPath, final String pathSpec) {
        coll.addHandler(getStaticResourcesHandler(resourcesPath, pathSpec));

        return this;
    }


    /**
     * @param app
     * @param pathSpec
     * @return <code>this</code>.
     */
    public WebServer withWebAppAt(final Application app, final String pathSpec) {
        coll.addHandler(getJerseyResourcesHandler(app, pathSpec));

        return this;

    }


    /**
     * @param app
     * @param pathSpec
     * @return a {@link ServletContextHandler}.
     */
    private static ServletContextHandler getJerseyResourcesHandler(final Application app, final String pathSpec) {
        final ServletContextHandler ctxHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        ctxHandler.addServlet(new ServletHolder(new ServletContainer(app)), pathSpec);

        return ctxHandler;
    }


    /**
     * @return a {@link ServletContextHandler}.
     */
    private static ServletContextHandler getServletContextHandler() {
        return new ServletContextHandler();
    }


    /**
     * @param resourcesPath
     * @param pathSpec
     * @return a {@link ServletContextHandler}.
     */
    private static ServletContextHandler getStaticResourcesHandler(final String resourcesPath, final String pathSpec) {
        final ServletContextHandler ctxHandler = getServletContextHandler();

        ctxHandler.addServlet(new ServletHolder(new DefaultServlet()), pathSpec);
        ctxHandler.setBaseResource(Resource.newClassPathResource(resourcesPath));

        return ctxHandler;
    }
}
