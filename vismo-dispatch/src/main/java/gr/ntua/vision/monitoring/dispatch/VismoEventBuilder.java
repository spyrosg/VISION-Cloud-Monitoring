package gr.ntua.vision.monitoring.dispatch;

import java.util.HashMap;
import java.util.Map;


/**
 * This is a convenience object helping generate events.
 */
public class VismoEventBuilder implements EventBuilder {
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
    VismoEventBuilder(final VismoEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }


    /**
     * @see gr.ntua.vision.monitoring.dispatch.EventBuilder#field(java.lang.String, java.lang.Object)
     */
    @Override
    public EventBuilder field(final String key, final Object value) {
        dict.put(key, value);
        return this;
    }


    /**
     * @see gr.ntua.vision.monitoring.dispatch.EventBuilder#send()
     */
    @Override
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
