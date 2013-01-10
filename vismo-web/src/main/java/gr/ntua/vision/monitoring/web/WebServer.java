package gr.ntua.vision.monitoring.web;

import java.util.HashSet;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.core.Application;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;


/**
 * A simple web server, for serving jersey resources.
 */
public class WebServer {
    /** the actual server. */
    private final Server          server;
    /** the set of resource to serve. */
    private final HashSet<Object> singletons = new HashSet<Object>();


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
     * Collect all resources an make an {@link Application} object, served by a servlet from the server's root.
     */
    private void buildWebApp() {
        if (singletons.isEmpty())
            throw new RuntimeException("you should specify at least one resource");

        final ResourceConfig rc = new DefaultResourceConfig();
        final ServletContextHandler root = new ServletContextHandler();

        rc.getSingletons().addAll(singletons);
        addServlet("/*", new ServletContainer(rc), root);
        root.setContextPath("/");
        server.setHandler(root);
    }


    /**
     * Convenience method to add a servlet to the context handler.
     * 
     * @param pathSpec
     *            the path to serve the servlet.
     * @param servlet
     *            the servlet.
     * @param handler
     *            the handler.
     */
    private static void addServlet(final String pathSpec, final HttpServlet servlet, final ServletContextHandler handler) {
        handler.addServlet(new ServletHolder(servlet), pathSpec);
    }
}
