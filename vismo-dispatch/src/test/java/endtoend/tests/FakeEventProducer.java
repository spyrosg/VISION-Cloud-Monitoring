package endtoend.tests;

import gr.ntua.vision.monitoring.dispatch.EventDispatcher;


/**
 * This is used to emulate an external to vismo event producer. It is assumed that the producers are running in the same (Linux)
 * host.
 */
public class FakeEventProducer extends Thread {
    /***/
    private final EventDispatcher dispatcher;
    /***/
    private final int             noEventsToSend;


    /**
     * @param dispatcher
     * @param noEventsToSend
     */
    public FakeEventProducer(final EventDispatcher dispatcher, final int noEventsToSend) {
        this.dispatcher = dispatcher;
        this.noEventsToSend = noEventsToSend;
    }


    /**
     * 
     */
    public void sendEvents() {
        for (int i = 0; i < noEventsToSend; ++i)
            sendReadEvent("ntua", "vassilis", "my-container", "foo-object");
    }


    /**
     * @param tenant
     * @param user
     * @param container
     * @param obj
     */
    private void sendReadEvent(final String tenant, final String user, final String container, final String obj) {
        dispatcher.newEvent().field("topic", "reads").field("type", "read").field("tenant", tenant).field("user", user)
                .field("container", container).field("obj", obj).send();
    }
}
