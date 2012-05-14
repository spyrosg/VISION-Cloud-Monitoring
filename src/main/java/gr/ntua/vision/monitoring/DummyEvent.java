package gr.ntua.vision.monitoring;

import java.util.Map;


/**
 *
 */
public class DummyEvent implements Event {
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
     * @see gr.ntua.vision.monitoring.Event#get(java.lang.String)
     */
    @Override
    public Object get(final String key) {
        return dict.get(key);
    }


    /**
     * @see gr.ntua.vision.monitoring.Event#timestamp()
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
        return "#<DummyEvent: " + dict;
    }
}
