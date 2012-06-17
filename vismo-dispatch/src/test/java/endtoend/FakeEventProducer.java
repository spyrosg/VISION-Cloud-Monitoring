package endtoend;

import gr.ntua.vision.monitoring.VismoEventDispatcher;

import java.util.UUID;
import java.util.logging.Logger;

import org.json.simple.JSONObject;


/**
 * This is used to emulate an external to vismo event producer. It is assumed that the producers are running in the same (Linux)
 * host.
 */
public class FakeEventProducer extends Thread {
    /***/
    private static final Logger        log = Logger.getLogger(FakeEventProducer.class.getName());
    /***/
    private final VismoEventDispatcher dispatcher;


    /**
     * @param dispatcher
     */
    public FakeEventProducer(VismoEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }


    /***/
    @SuppressWarnings("unchecked")
    private void sendEvent() {
        final JSONObject o = new JSONObject();

        o.put("timestamp", System.currentTimeMillis());
        o.put("topic", "my-topic");
        o.put("originating-service", "fake-event-producer");
        o.put("originating-ip", "localhost");
        o.put("id", UUID.randomUUID().toString());

        log.finer("sending " + o);

        // sock.send(o.toJSONString().getBytes(), 0);
    }


    /**
     * @param tenant
     * @param user
     * @param container
     * @param obj
     */
    private void sendReadEvent(final String tenant, final String user, final String container, final String obj) {
        dispatcher.field("topic", "reads").field("type", "read").field("tenant", tenant).field("user", user)
                .field("container", container).field("obj", obj).send();
    }


    /**
     * 
     */
    @Override
    public void run() {
    }


    /**
     * 
     */
    public void sendEvents() {
        start();
    }
}
