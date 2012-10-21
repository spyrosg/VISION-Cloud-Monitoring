package gr.ntua.vision.monitoring.web;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.notify.EventHandler;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jetty.servlets.EventSource;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public final class VismoEventsSource implements EventSource, EventHandler {
    /***/
    private static final Logger log    = LoggerFactory.getLogger(VismoEventsSource.class);
    /***/
    private volatile boolean    closed = false;
    /***/
    private volatile Emitter    emitter;


    /**
     * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void handle(final Event e) {
        if (closed)
            return;

        @SuppressWarnings("rawtypes")
        final Map m = (Map) e.get("!dict");
        final String s = JSONObject.toJSONString(m);

        try {
            send(s);
        } catch (final IOException e1) {
            log.error("error sending event", e1);
            onClose();
        }
    }


    /**
     * @see org.eclipse.jetty.servlets.EventSource#onClose()
     */
    @Override
    public void onClose() {
        log.debug("onClose for emitter: {}", emitter);
        closed = true;
        emitter.close();
    }


    /**
     * @see org.eclipse.jetty.servlets.EventSource#onOpen(org.eclipse.jetty.servlets.EventSource.Emitter)
     */
    @Override
    public void onOpen(final Emitter emitter) {
        log.debug("onOpen for emitter: {}", emitter);
        this.emitter = emitter;
        this.closed = false;
    }


    /**
     * @param msg
     * @throws IOException
     */
    private void send(final String msg) throws IOException {
        emitter.data(msg + "\n\n");
    }
}
