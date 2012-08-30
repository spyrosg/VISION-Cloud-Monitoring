package gr.ntua.vision.monitoring;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a convenience object helping generate events.
 */
public class EventBuilder {
    /** this is used to keep track of the event fields. */
    private final Map<String, Object> dict = new HashMap<String, Object>();
    /** the dispatcher. */
    private final EventDispatcher     dispatcher;


    /**
     * Constructor.
     * 
     * @param dispatcher
     *            the dispatcher.
     */
    public EventBuilder(final EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }


    /**
     * Append to the current event a new key/value pair.
     * 
     * @param key
     *            the key.
     * @param value
     *            the value.
     * @return <code>this</code>.
     */
    public EventBuilder field(final String key, final Object value) {
        dict.put(key, value);
        return this;
    }


    /**
     * Send the event to the locally running <code>vismo</code> instance.
     */
    public void send() {
        dispatcher.send(dict);
    }
}