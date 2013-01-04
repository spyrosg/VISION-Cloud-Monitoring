package gr.ntua.vision.monitoring.dispatch;

import java.util.HashMap;
import java.util.Map;


/**
 * This is a convenience object helping generate events.
 */
public class EventBuilder {
    /** this is used to keep track of the event fields. */
    private final Map<String, Object>  dict = new HashMap<String, Object>();
    /** the event dispatcher object. */
    private final VismoEventDispatcher dispatcher;


    /**
     * Constructor.
     * 
     * @param dispatcher
     *            the event dispatcher object.
     */
    EventBuilder(final VismoEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }


    /**
     * Append a new field (key/value) pair to the event.
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
        clearEvent();
    }


    /**
     * Remove any fields from the event.
     */
    private void clearEvent() {
        dict.clear();
    }
}
