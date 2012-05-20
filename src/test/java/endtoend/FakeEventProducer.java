package endtoend;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 * This is used to emulate an external to vismo event producer. It is assumed that the producers are running in the same (Linux)
 * host.
 */
public class FakeEventProducer {
    /***/
    private final String eventsEntryPoint;
    /***/
    private final Logger log = LoggerFactory.getLogger(FakeEventProducer.class);
    /***/
    private final int    noEventsToSend;
    /***/
    private final Socket sock;


    /**
     * Constructor.
     * 
     * @param ctx
     * @param eventsEntryPoint
     * @param noEventsToSend
     */
    public FakeEventProducer(final ZContext ctx, final String eventsEntryPoint, final int noEventsToSend) {
        this.eventsEntryPoint = eventsEntryPoint;
        this.sock = ctx.createSocket(ZMQ.PUSH);
        this.sock.setLinger(0);
        this.noEventsToSend = noEventsToSend;
    }


    /***/
    public void sendEvents() {
        for (int i = 0; i < noEventsToSend; ++i)
            sendEvent("foo");
    }


    /***/
    public void start() {
        log.debug("connecting to endpoint={}", eventsEntryPoint);
        sock.connect(eventsEntryPoint);
    }


    /***/
    public void stop() {
        sock.close();
    }


    /**
     * @param s
     */
    @SuppressWarnings("unchecked")
    private void sendEvent(final String s) {
        log.debug("sending: {}", s);
        final JSONObject o = new JSONObject();

        o.put("timestamp", System.currentTimeMillis());
        o.put("val", s);

        sock.send(o.toJSONString().getBytes(), 0);
    }
}
