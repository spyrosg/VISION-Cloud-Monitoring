package endtoend;

import java.util.Random;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 * This is used to emulate a vismo instance. Consumers should be able to use the <code>vismo-events</code> lib to register and
 * receive events.
 */
public class FakeMonitoringInstance {
    /***/
    private static final Logger log = LoggerFactory.getLogger(FakeMonitoringInstance.class);
    /***/
    private static final Random rng = new Random();
    /***/
    private final int           noEventsToSend;
    /***/
    private final Socket        sock;
    /** the list of topics for the events to generate. */
    private final String[]      topics;


    /**
     * Constructor.
     * 
     * @param ctx
     * @param eventsEntryPoint
     * @param noEventsToSend
     * @param topics
     */
    public FakeMonitoringInstance(final ZContext ctx, final String eventsEntryPoint, final int noEventsToSend,
            final String[] topics) {
        this.sock = ctx.createSocket(ZMQ.PUB);
        this.sock.setLinger(0);
        this.sock.bind(eventsEntryPoint);
        this.noEventsToSend = noEventsToSend;
        this.topics = topics;
        log.debug("listening on endpoint={}", eventsEntryPoint);
    }


    /***/
    public void sendEvents() {
        for (int i = 0; i < noEventsToSend; ++i)
            sendEvent();
    }


    /***/
    @SuppressWarnings("unchecked")
    private void sendEvent() {
        final JSONObject o = new JSONObject();
        final String topic = topics[choice(topics.length)];

        o.put("timestamp", System.currentTimeMillis());
        o.put("topic", topic);
        o.put("originating-service", "fake-monitoring-instance");
        o.put("originating-ip", "localhost");
        o.put("id", UUID.randomUUID().toString());

        log.trace("sending {}", o);

        sock.send(topic.getBytes(), ZMQ.SNDMORE);
        sock.send(o.toJSONString().getBytes(), 0);
    }


    /**
     * @param n
     * @return a
     */
    private static int choice(final int n) {
        return rng.nextInt(n);
    }
}
