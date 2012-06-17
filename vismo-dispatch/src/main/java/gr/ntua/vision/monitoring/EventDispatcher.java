package gr.ntua.vision.monitoring;

import java.util.Map;


/**
 * This interface is used to denote any objects that are willing to produce monitoring events. Events can come from any source and
 * should be posted to the locally running <code>Vismo</code> instance.
 */
public interface EventDispatcher {
    /**
     * @param map
     */
    void send(final Map<String, Object> map);
}
