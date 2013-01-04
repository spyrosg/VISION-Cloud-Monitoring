package gr.ntua.vision.monitoring.web;

import gr.ntua.vision.monitoring.notify.VismoEventRegistry;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 */
@SuppressWarnings("serial")
public class VisionEventsServlet extends EventSourceServlet {
    /***/
    private static final Logger      log = LoggerFactory.getLogger(VisionEventsServlet.class);
    /***/
    private final VismoEventRegistry registry;


    /**
     * @param registry
     */
    public VisionEventsServlet(final VismoEventRegistry registry) {
        this.registry = registry;
    }


    /**
     * @see org.eclipse.jetty.servlets.EventSourceServlet#newEventSource(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected EventSource newEventSource(final HttpServletRequest request) {
        final VismoEventsSource source = new VismoEventsSource();

        log.debug("event request: {}", request);
        log.debug("registering new handler/event source: {}", source);
        registry.registerToAll(source);

        return source;
    }
}
