package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;

import java.util.Map;

import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 * The event distributor (TODO: better name) is used to pass events received in localhost to all <em>Vismo</em> consumers. Each
 * event is sent according to its accompanying topic. NOTE: the running thread does not block, which means that for now, if there
 * is no receiving end (no connected consumers) the event is dropped.
 */
public class EventDistributor implements EventListener {
    /** the log target. */
    private static final Logger log = LoggerFactory.getLogger(EventDistributor.class);
    /** the socket. */
    private final Socket        sock;


    /**
     * Constructor.
     * 
     * @param ctx
     *            the zmq context.
     * @param distributionPoint
     */
    public EventDistributor(final ZContext ctx, final String distributionPoint) {
        this.sock = ctx.createSocket(ZMQ.PUB);
        this.sock.setLinger(0);
        this.sock.setSendTimeOut(0); // FIXME: non-blocking for now
        this.sock.bind(distributionPoint);
        log.debug("listening on endpoint={}", distributionPoint);
    }


    /**
     * @see gr.ntua.vision.monitoring.EventListener#notify(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void notify(final Event e) {
        @SuppressWarnings("rawtypes")
        final Map dict = (Map) e.get("!dict");
        final String msg = JSONValue.toJSONString(dict);
        final String topic = (String) dict.get("topic");

        // TODO: get back to this. Should we block or should be drop?
        sock.send(topic.getBytes(), ZMQ.SNDMORE);
        final boolean success = sock.send(msg.getBytes(), 0);
        log.trace("sent: {}", success ? "ok" : "dropped");
    }
}
