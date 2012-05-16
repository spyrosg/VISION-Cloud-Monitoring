package gr.ntua.vision.monitoring.events;

/**
 *
 */
public interface Event {
    /**
     * @param key
     * @return
     */
    Object get(String key);


    /**
     * @return
     */
    long timestamp();
}
