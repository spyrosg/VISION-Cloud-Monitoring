package endtoend.tests;

import gr.ntua.monitoring.sockets.Socket;

import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import org.json.simple.JSONObject;


/**
 * This is used to emulate a vismo instance. Consumers should be able to use the <code>vismo-events</code> lib to register and
 * receive events.
 */
public class FakeMonitoringInstance {
    /***/
    private static final Logger log = Logger.getLogger(FakeMonitoringInstance.class.getName());
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
     * @param sock
     * @param noEventsToSend
     * @param topics
     */
    public FakeMonitoringInstance(final Socket sock, final int noEventsToSend, final String[] topics) {
        this.sock = sock;
        this.noEventsToSend = noEventsToSend;
        this.topics = topics;
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

        log.fine("sending " + o);
        sock.send(topic + " " + o.toJSONString());
    }


    /**
     * @param n
     * @return a pseudo random number in the range [0, n).
     */
    private static int choice(final int n) {
        return rng.nextInt(n);
    }
}
