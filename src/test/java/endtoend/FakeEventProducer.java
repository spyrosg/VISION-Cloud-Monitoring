package endtoend;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 *
 */
public class FakeEventGeneratorServer {
    /***/
    private static final int    NO_EVENTS_TO_SENT = 10;
    /***/
    private static final byte[] TO_ALL            = "*".getBytes();
    /***/
    private final String        eventsEndPoint;
    /***/
    private final Logger        log               = LoggerFactory.getLogger(FakeEventGeneratorServer.class);
    /***/
    private final Socket        sock;


    /**
     * @param ctx
     * @param zmqPort
     */
    public FakeEventGeneratorServer(final ZContext ctx, final String zmqPort) {
        this.eventsEndPoint = zmqPort;
        this.sock = ctx.createSocket(ZMQ.ROUTER);
        this.sock.setLinger(0);
    }


    /**
     * 
     */
    public void sendEvents() {
        for (int i = 0; i < NO_EVENTS_TO_SENT; ++i)
            sendEvent("foo");
    }


    /**
     * 
     */
    public void start() {
        log.debug("binding to endpoint={}", eventsEndPoint);
        sock.bind(eventsEndPoint);
    }


    /**
     * 
     */
    public void stop() {
        sock.close();
    }


    /**
     * @param s
     */
    private void sendEvent(final String s) {
        log.debug("sending: {}", s);
        final JSONObject o = new JSONObject();

        o.put("timestamp", System.currentTimeMillis());
        o.put("val", s);

        sock.send(TO_ALL, ZMQ.SNDMORE);
        sock.send(o.toJSONString().getBytes(), 0);
    }
}
