package helpers;

import gr.ntua.vision.monitoring.dispatch.EventBuilder;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class InMemoryEventBuilder implements EventBuilder {
    /***/
    private static final Logger           log = LoggerFactory.getLogger(InMemoryEventBuilder.class);
    /***/
    private final InMemoryEventDispatcher dispatcher;
    /***/
    private final HashMap<String, Object> map = new HashMap<String, Object>();


    /**
     * Constructor.
     * 
     * @param dispatcher
     */
    public InMemoryEventBuilder(final InMemoryEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }


    /**
     * @see gr.ntua.vision.monitoring.dispatch.EventBuilder#field(java.lang.String, java.lang.Object)
     */
    @Override
    public EventBuilder field(final String key, final Object value) {
        map.put(key, value);
        return this;
    }


    /**
     * @see gr.ntua.vision.monitoring.dispatch.EventBuilder#send()
     */
    @Override
    public void send() {
        dispatcher.send(new HashMap<String, Object>(map));
        map.clear();
    }
}
