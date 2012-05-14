package gr.ntua.vision.monitoring;

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
