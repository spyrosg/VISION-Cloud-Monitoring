package endtoend;

import java.util.UUID;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ.Socket;


/**
 * This is used to emulate an external to vismo event producer. It is assumed that the producers are running in the same (Linux)
 * host.
 */
public class FakeEventProducer {
    /***/
    private static final Logger log = LoggerFactory.getLogger(FakeEventProducer.class);
    /***/
    private final int           noEventsToSend;
    /***/
    private final Socket        sock;


    /**
     * Constructor.
     * 
     * @param sock
     *            the socket to use.
     * @param noEventsToSend
     */
    public FakeEventProducer(final Socket sock, final int noEventsToSend) {
        this.sock = sock;
        this.noEventsToSend = noEventsToSend;
    }


    /***/
    public void sendEvents() {
        for (int i = 0; i < noEventsToSend; ++i)
            sendEvent(generateEvent());
    }


    /***/
    public void stop() {
        sock.close();
    }


    /**
     * @param o
     */
    private void sendEvent(final JSONObject o) {
        log.trace("sending {}", o);
        sock.send(((String) o.get("topic") + " " + o.toJSONString()).getBytes(), 0);
    }


    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    private static JSONObject generateEvent() {
        final JSONObject o = new JSONObject();

        o.put("timestamp", System.currentTimeMillis());
        o.put("topic", "my-topic");
        o.put("originating-service", "fake-event-producer");
        o.put("originating-ip", "localhost");
        o.put("id", UUID.randomUUID().toString());

        return o;
    }
}
