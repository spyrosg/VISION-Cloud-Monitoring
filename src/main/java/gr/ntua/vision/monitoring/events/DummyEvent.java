package gr.ntua.vision.monitoring.events;

import java.util.Map;


/**
 *
 */
class DummyEvent implements Event {
    /***/
    @SuppressWarnings("rawtypes")
    private final Map dict;


    /**
     * @param dict
     */
    public DummyEvent(@SuppressWarnings("rawtypes") final Map dict) {
        this.dict = dict;
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#get(java.lang.String)
     */
    @Override
    public Object get(final String key) {
        if (key.equals("!dict"))
            return dict;

        return dict.get(key);
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#timestamp()
     */
    @Override
    public long timestamp() {
        return (Long) dict.get("timestamp");
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<DummyEvent: " + dict + ">";
    }
}
