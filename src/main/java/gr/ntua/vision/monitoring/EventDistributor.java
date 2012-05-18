package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.EventListener;

import java.util.Map;

import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 *
 */
public class EventDistributor implements EventListener {
    /***/
    private static final Logger log = LoggerFactory.getLogger(EventDistributor.class);
    /***/
    private final Socket        sock;


    /**
     * @param ctx
     * @param distributionPoint
     */
    public EventDistributor(final ZContext ctx, final String distributionPoint) {
        this.sock = ctx.createSocket(ZMQ.PUSH);
        this.sock.setLinger(0);
        // this.sock.setSendTimeOut(0);
        this.sock.bind(distributionPoint);
        log.debug("listening to endpoint={}", distributionPoint);
    }


    /**
     * @see gr.ntua.vision.monitoring.events.EventListener#notify(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void notify(final Event e) {
        @SuppressWarnings("rawtypes")
        final Map dict = (Map) e.get("!dict");
        final String msg = JSONValue.toJSONString(dict);

        if (this.sock.send(msg.getBytes(), 0))
            log.trace("sent");
        else
            log.trace("dropped event");
    }
}
