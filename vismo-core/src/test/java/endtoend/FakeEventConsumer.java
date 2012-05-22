package endtoend;

import gr.ntua.vision.monitoring.events.EventHandler;
import gr.ntua.vision.monitoring.events.EventRegistry;


/**
 *
 */
public class FakeEventConsumer {
    /***/
    private final EventRegistry registry;


    /**
     * Constructor.
     * 
     * @param registry
     */
    public FakeEventConsumer(final EventRegistry registry) {
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
