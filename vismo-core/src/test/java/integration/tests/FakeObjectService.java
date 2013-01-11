package integration.tests;

import gr.ntua.vision.monitoring.dispatch.VismoEventDispatcher;


/**
 * 
 */
public class FakeObjectService {
    /***/
    private final VismoEventDispatcher dispatcher;


    /**
     * Constructor.
     * 
     * @param dispatcher
     */
    public FakeObjectService(final VismoEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }


    /**
     * @param noReadEvents
     */
    public void sendReadEvents(final int noReadEvents) {
        for (int i = 0; i < noReadEvents; ++i)
            dispatcher.newEvent().field("object", "foo").field("container", "bar").field("user", "bill").field("tenant", "ntua")
                    .field("operation", "GET").field("content-size", 1000).send();

    }


    /**
     * @param noWriteEvents
     */
    public void sendWriteEvents(final int noWriteEvents) {
        for (int i = 0; i < noWriteEvents; ++i)
            dispatcher.newEvent().field("object", "foo").field("container", "bar").field("user", "bill").field("tenant", "ntua")
                    .field("operation", "PUT").field("content-size", 1000).send();

    }
}
