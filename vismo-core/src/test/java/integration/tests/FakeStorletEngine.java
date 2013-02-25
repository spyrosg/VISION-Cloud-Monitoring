package integration.tests;

import gr.ntua.vision.monitoring.dispatch.EventDispatcher;


/**
 * 
 */
public class FakeStorletEngine {
    /***/
    private static final String   SERVICE_NAME = "SRE";
    /***/
    private final EventDispatcher dispatcher;
    /***/
    private final String          SRE_ID       = "my-sre-id";


    /**
     * @param dispatcher
     */
    public FakeStorletEngine(final EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }


    /**
     * @param storletId
     * @param activationId
     * @param message
     */
    public void sendEvent(final String storletId, final String activationId, final String message) {
        dispatcher.newEvent().field("originating-service", SERVICE_NAME).field("sre-id", SRE_ID).field("storlet-id", storletId)
                .field("activation-id", activationId).field("message", message).send();
    }
}
