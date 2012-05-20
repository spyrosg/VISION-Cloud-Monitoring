package endtoend;

import gr.ntua.vision.monitoring.events.EventHandler;


/**
 *
 */
public class FakeEventConsumer {
    /***/
    private final EventRegister registry;


    /**
     * Constructor.
     * 
     * @param registry
     */
    public FakeEventConsumer(final EventRegister registry) {
        this.registry = registry;
    }


    /**
     * @param topic
     * @param handler
     */
    public void register(final String topic, final EventHandler handler) {
        registry.register(topic, handler);
    }


    /**
     * @param handler
     */
    public void registerToAll(final EventHandler handler) {
        register("", handler);
    }
}
